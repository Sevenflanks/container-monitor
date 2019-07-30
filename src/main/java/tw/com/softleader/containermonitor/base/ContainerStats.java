package tw.com.softleader.containermonitor.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
    "recordTime", "id", "name", "image", "network", "cpuPerc", "memUsage", "memLimit", "netIn", "netOut", "blockIn", "blockOut", "jvmMetric"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerStats {

  private String id;
  private String name;
  private String image;
  private String network;
  private double cpuPerc; // %
  private double memUsage; // B
  private double memLimit; // B
  private double netIn; // B
  private double netOut; // B
  private double blockIn; // B
  private double blockOut;  // B

  private LocalDateTime recordTime;

  @JsonUnwrapped(prefix = "jvm.")
  private JvmMetric jvmMetric;

}
