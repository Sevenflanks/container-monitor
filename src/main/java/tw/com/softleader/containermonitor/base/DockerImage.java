package tw.com.softleader.containermonitor.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DockerImage {

  private String imageId;
  private String name;

}
