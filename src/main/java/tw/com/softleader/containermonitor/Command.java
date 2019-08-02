package tw.com.softleader.containermonitor;

import java.util.List;

public interface Command {

  String S = "::";

  List<String> curlJvmMetrics(String containerId);

  List<String> dockerPs();

  List<String> dockerStats();
}
