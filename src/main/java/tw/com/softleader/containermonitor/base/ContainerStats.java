package tw.com.softleader.containermonitor.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import lombok.experimental.Wither;

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
@Wither
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

  @JsonIgnore
  public String getSaveFilename() {
    return "record." + image.replaceAll("[:/]", "_") + ".csv";
  }
}
