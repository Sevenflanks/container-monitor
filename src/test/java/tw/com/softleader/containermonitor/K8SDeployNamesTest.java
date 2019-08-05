package tw.com.softleader.containermonitor;

import lombok.SneakyThrows;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class K8SDeployNamesTest {

  @Test
  public void toK8sDeployNames() {
    try (Stream<String> stream = classLoaderResource("log_docker_stats")) {
      stream
          .map(line -> line.split("::")[1])
          .map(K8sDeployNames::from)
          .filter(Optional::isPresent)
          .forEach(System.out::println);
    }
  }

  @SneakyThrows
  private Stream classLoaderResource(String name) {
    return Files.lines(Paths.get(ClassLoader.getSystemResource(name).toURI()));
  }
}
