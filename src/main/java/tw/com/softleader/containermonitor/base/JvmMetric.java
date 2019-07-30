package tw.com.softleader.containermonitor.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JvmMetric {

  @JsonProperty("mem")
  private Double mem;

  @JsonProperty("mem.free")
  private Double memFree;

  @JsonProperty("processors")
  private Double processors;

  @JsonProperty("instance.uptime")
  private Double instanceUptime;

  @JsonProperty("uptime")
  private Double uptime;

  @JsonProperty("systemload.average")
  private Double systemloadAverage;

  @JsonProperty("heap.committed")
  private Double heapCommitted;

  @JsonProperty("heap.init")
  private Double heapInit;

  @JsonProperty("heap.used")
  private Double heapUsed;

  @JsonProperty("heap")
  private Double heap;

  @JsonProperty("nonheap.committed")
  private Double nonheapCommitted;

  @JsonProperty("nonheap.init")
  private Double nonheapInit;

  @JsonProperty("nonheap.used")
  private Double nonheapUsed;

  @JsonProperty("nonheap")
  private Double nonheap;

  @JsonProperty("threads.peak")
  private Double threadsPeak;

  @JsonProperty("threads.daemon")
  private Double threadsDaemon;

  @JsonProperty("threads.totalStarted")
  private Double threadsTotalStarted;

  @JsonProperty("threads")
  private Double threads;

  @JsonProperty("classes")
  private Double classes;

  @JsonProperty("classes.loaded")
  private Double classesLoaded;

  @JsonProperty("classes.unloaded")
  private Double classesUnloaded;

  @JsonProperty("gc.ps_scavenge.count")
  private Double gcPsScavengeCount;

  @JsonProperty("gc.ps_scavenge.time")
  private Double gcPsScavengeTime;

  @JsonProperty("gc.ps_marksweep.count")
  private Double gcPsMarksweepCount;

  @JsonProperty("gc.ps_marksweep.time")
  private Double gcPsMarksweepTime;

  @JsonProperty("httpsessions.max")
  private Double httpsessionsMax;

  @JsonProperty("httpsessions.active")
  private Double httpsessionsActive;

  @JsonProperty("gauge.response.metrics")
  private Double gaugeResponseMetrics;

  @JsonProperty("counter.status.200.metrics")
  private Double counterStatus200Metrics;



}
