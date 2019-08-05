package tw.com.softleader.containermonitor.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DockerPs {

  private String containerId;
  private String imageId;
  private String network;

}
