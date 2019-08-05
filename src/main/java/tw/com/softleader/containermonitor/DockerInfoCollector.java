package tw.com.softleader.containermonitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tw.com.softleader.containermonitor.base.BytesUtils;
import tw.com.softleader.containermonitor.base.ContainerStats;
import tw.com.softleader.containermonitor.base.JvmMetric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static tw.com.softleader.containermonitor.Command.*;

@Slf4j
@Service
@Profile("!test")
public class DockerInfoCollector implements ApplicationRunner {

  @Value("${container.monitor.record.file.path.root}") private String fileRoot;

  @Value("${container.monitor.run.cron_job}") private String cronJob;

  @Value("${container.monitor.run.namespaces}") Collection<String> namespaces;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private CsvMapper csvMapper;

  @Autowired private TaskScheduler taskScheduler;

  @Autowired private Command command;

  @Override public void run(ApplicationArguments args) throws Exception {
    if (namespaces.size() > 0) {
      log.info("detected specified namespaces: {}", namespaces);
    }
    if (StringUtils.isEmpty(cronJob)) {
      log.info("running once");
      doCollect();
    } else {
      // 如果有輸入cronJob則依cronjob的方式跑
      log.info("running with cron job, {}", cronJob);
      taskScheduler.schedule(Unchecked.runnable(this::doCollect), new CronTrigger(cronJob));
    }
  }

  private void doCollect() throws IOException {
    final LocalDateTime now = LocalDateTime.now();
    log.info("start collecting docker info");
    callDockerStats(now) // 取得 docker stats
        .map(this::loadJvmMetric) // 取的 jvm metrics
        .forEach(container -> {
          // 儲存路徑
          Path root = Paths.get(fileRoot);
          Path path = root.resolve(container.getSaveFilename());
          log.debug("saving csv file: {}", path);
          // 檢查是否為新建
          boolean exists = path.toFile().exists();
          try (OutputStream outputStream = Files
              .newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                  StandardOpenOption.APPEND)) {
            // 資料夾是否存在
            if (root.toFile().exists()) {
              Files.createDirectories(root);
            }

            // 輸出格式為 csv
            CsvSchema csvSchema = csvMapper.schemaFor(ContainerStats.class);
            if (exists) {
              csvMapper.writer(csvSchema).writeValue(outputStream, container);
            } else {
              // 若為新建，該次寫入要一併將title寫入
              csvMapper.writer(csvSchema.withUseHeader(true)).writeValue(outputStream, container);
            }
          } catch (Exception e) {
            log.error("failed to write record file, id:{}, name:{}, cause:{}", container.getId(),
                container.getName(), e.getMessage());
          }
        });

    log.info("finish collect docker info, processing time:{}ms", NumberFormat.getIntegerInstance()
        .format(Duration.between(now, LocalDateTime.now()).toMillis()));
  }

  /**
   * 取得 jvm 運行資訊 by container
   */
  private ContainerStats loadJvmMetric(ContainerStats container) {
    try {
      // 呼叫 curl actuator endpoint
      final List<String> cmdAndArgs = command.curlJvmMetrics(container.getId());
      log.debug("running curl jvm metrics command: {}", cmdAndArgs);

      // 將結果轉為物件
      final ProcessBuilder pb = new ProcessBuilder(cmdAndArgs);
      final Process process = pb.start();
      final BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()));
      final JvmMetric jvmMetric = reader.lines()
          .map(Unchecked.function(json -> objectMapper.readValue(json, JvmMetric.class)))
          .findAny()
          .orElse(null);
      container.setJvmMetric(jvmMetric);
    } catch (Exception e) {
      log.error("failed to load jvm metric, id:{}, name:{}, cause:{}", container.getId(),
          container.getName(), e.getMessage());
    }
    return container;
  }

  /**
   * 取得 docker ps 資訊, 並以Map包裝(ContainerId為key)
   */
  private Map<String, String[]> callDockerPs() throws IOException {
    // 呼叫 docker ps
    final List<String> cmdAndArgs = command.dockerPs();

    // 將結果轉換為 map
    final ProcessBuilder pb = new ProcessBuilder(cmdAndArgs);
    final Process process = pb.start();
    final BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream()));

    // key=containerId, value=[image, networks]
    log.debug("running docker ps command: {}", cmdAndArgs);
    return reader.lines()
        .map(line -> {
          log.trace(line);
          return line;
        })
        .map(line -> line.split(S))
        .collect(toPsMap());
  }

  Collector<String[], ?, Map<String, String[]>> toPsMap() {
    return Collectors.toMap(r -> r[0], r -> {
      if (r.length > 2) {
        return new String[] {r[1], r[2]};
      } else {
        // 有可能會缺少一些參數，屆時不抓近來
        return new String[] {r[1], "N/A"};
      }
    });
  }


  /**
   * 取得 docker stats 資訊
   */
  private Stream<ContainerStats> callDockerStats(LocalDateTime recordTime) throws IOException {
    // 呼叫 docker stats
    final List<String> cmdAndArgs = command.dockerStats();

    // 呼叫 docker ps
    final Map<String, String[]> dockerPs = callDockerPs();

    // 將結果轉換為物件
    final ProcessBuilder pb = new ProcessBuilder(cmdAndArgs);
    final Process process = pb.start();
    final BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream()));
    log.debug("running docker stats command: {}", cmdAndArgs);
    return reader.lines()
        .map(line -> {
          log.trace(line);
          return line;
        })
        .map(line -> toContainer(line, dockerPs))
        .map(container -> container.withRecordTime(recordTime)).filter(this::isMonitorTarget);
  }

  boolean isMonitorTarget(ContainerStats c) {
    if (c.getImage().startsWith("ibmcom")) {
      return false;
    }
    if (namespaces.size() > 0 && c.getK8sDeployNames().isPresent()) {
      return namespaces.stream()
          .anyMatch(ns -> ns.equals(c.getK8sDeployNames().get().getNamespace()));
    }
    return true;
  }

  ContainerStats toContainer(String line, Map<String, String[]> dockerPs) {
    String[] dockerStats = line.split(S);
    String id = dockerStats[0];
    String cpu = dockerStats[2];
    String[] mem = dockerStats[3].split(" / ");
    String[] net = dockerStats[4].split(" / ");
    String[] block = dockerStats[5].split(" / ");
    String[] dockerInfo = dockerPs.get(id);

    return ContainerStats.builder()
        .id(id)
        .name(dockerStats[1])
        .k8sDeployNames(K8sDeployNames.from(dockerStats[1]))
        .image(dockerInfo[0])
        .network(dockerInfo[1])
        .cpuPerc(Double.valueOf(cpu.substring(0, cpu.length() - 1)))
        .memUsage(BytesUtils.toB(mem[0]))
        .memLimit(BytesUtils.toB(mem[1]))
        .netIn(BytesUtils.toB(net[0]))
        .netOut(BytesUtils.toB(net[1]))
        .blockIn(BytesUtils.toB(block[0]))
        .blockOut(BytesUtils.toB(block[1]))
        .build();
  }

}
