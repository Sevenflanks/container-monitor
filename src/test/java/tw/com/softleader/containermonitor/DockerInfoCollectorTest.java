package tw.com.softleader.containermonitor;

import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import tw.com.softleader.containermonitor.base.DockerImage;
import tw.com.softleader.containermonitor.base.DockerPs;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import static tw.com.softleader.containermonitor.Command.S;

public class DockerInfoCollectorTest {

  private DockerInfoCollector collector;
  private Map<String, DockerPs> dockerPs;
  private Map<String, DockerImage> dockerImageLs;

  @Before
  public void init() {
    collector = new DockerInfoCollector();
    collector.namespaces = Lists.newArrayList( "jasmine-prod");
    try (Stream<String> stream = classLoaderResource("log_docker_ps")) {
      dockerPs = stream.map(line -> line.split(S)).collect(collector.toPsMap());
    }
    try (Stream<String> stream = classLoaderResource("log_docker_image_ls")) {
      dockerImageLs = stream.map(line -> line.split(S)).collect(collector.toImageMap());
    }
  }

  @Test
  public void toContainer() {
    try (Stream<String> stream = classLoaderResource("log_docker_stats")) {
      stream
          .map(line -> collector.toContainer(line, dockerPs, dockerImageLs))
          .filter(collector::isMonitorTarget)
          .forEach(
              c ->
                  System.out.println(
                      String.format(
                          "name: %s, image: %s, filename: %s",
                          c.getName(), c.getImage(), c.getSaveFilename()))); // TODO 要確認一下為什麼 image 都是亂碼
    }
  }

  @SneakyThrows
  private Stream classLoaderResource(String name) {
    return Files.lines(Paths.get(ClassLoader.getSystemResource(name).toURI()));
  }

}
