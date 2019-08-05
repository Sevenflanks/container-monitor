package tw.com.softleader.containermonitor;

import java.util.List;

public interface Command {

  String S = "::";

  String AUTH =
      "Authorization: Bearer {\"username\":\"container_monitor\",\"password\":\"cm1234\",\"authorities\":[{\"authority\":\"ACTUATOR\"}],\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true,\"enabled\":true,\"actualUsername\":null}";

  List<String> curlJvmMetrics(String containerId);

  List<String> dockerPs();

  List<String> dockerImageLs();

  List<String> dockerStats();
}
