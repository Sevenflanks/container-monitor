package tw.com.softleader.containermonitor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 此 class 實作了解析 k8s deployment 的 names, read more details:
 * https://jimmysong.io/kubernetes-handbook/practice/monitor.html
 */
@Builder
@Getter
@Slf4j
public class K8sDeployNames {

  public static final Optional<K8sDeployNames> from(@NonNull String containerName) {
    String[] slice = containerName.split("_");
    if (slice.length != 6) {
      log.debug("k8s 控制的 container 一定是 6 個底線連接起來, 實際上只有 {} 個", slice.length);
      return Optional.empty();
    }
    if (!"k8s".equals(slice[0].toLowerCase())) {
      log.debug("k8s 控制的 container 開頭一定是 k8s, 實際上卻是 '{}'", slice[0]);
      return Optional.empty();
    }
    String[] pod = slice[2].split("-");
    if (pod.length < 3) {
      log.debug("部署 k8s deploy 的話, pod name 至少有 3 段: deploy, rs, pod, 實際上卻是 {}", pod);
      return Optional.empty();
    }

    K8sDeployNamesBuilder builder = K8sDeployNames.builder()
            .containerNamePrefix(slice[0])
            .containerName(slice[1])
            .deploymentName(
                    Arrays.stream(pod).limit(pod.length - 3).collect(Collectors.joining("-")))
            .replicaSetName(pod[pod.length - 2])
            .podFullName(pod[pod.length - 1])
            .namespace(slice[3])
            .podUID(slice[4])
            .restartCount(Integer.parseInt(slice[5]));

    return Optional.of(builder.build());
  }

  private String containerNamePrefix;
  private String containerName;
  private String podFullName;
  private String computeHash;
  private String deploymentName;
  private String replicaSetName;
  private String namespace;
  private String podUID;
  private int restartCount;
}
