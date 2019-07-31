# Container Monitor
這是一個用於紀錄機器上 docker stats 的 java app

### run
- [x] run as java app
```
# 透過Maven啟動，紀錄寫在當前目錄
mvn spring-boot:run

# 透過Maven啟動，記錄檔寫在指定目錄
mvn spring-boot:run -Dcontainer.monitor.record.file.path.root=/logs/
```

- [X] run as docker image
```
# 透過Docker啟動，紀錄寫出至mount目錄
docker run \
       --rm \
       -v //var/run/docker.sock:/var/run/docker.sock \
       -v "C:/develop/container-monitor:/var/softleader_home/container_monitor/" \
       softleader/container-monitor:v0.1.0
```

- [X] run as helm chart cronjob
```
呃..雖然提供了可是Matt操作太快我沒記下來
```

### properties
| name                                    | value sample                 |
|-----------------------------------------|------------------------------|
| container.monitor.record.file.path.root | `~/logs`, `C:/logs`, `logs/` |

### JVM Metric support
本 App 可以蒐集 JVM 運行資訊，但需要該Container的JavaAPP具有Actuator，並設定以下參數
```
endpoints.metrics.enabled=true
management.security.enabled=false
```

### behavior
1. 啟動後，會立即蒐集當前環境的 docker ps, docker stats 資訊
2. 如果情況允許，將會蒐集Jvm運行資訊 (需要該Container有開出Actuator的Metrics Endpoint)
3. 蒐集完一切資訊後，根據 image name 產生運行記錄檔 (.csv)


### Tips
1. 可以用Excel開啟CSV繪製圖表
2. 於Excel中可以利用格式化將容量數字轉換為人類看得懂的樣式
  - `[<1000000]#,##0.00," KB";[<1000000000]#,##0.00,," MB";#,##0.00,,," GB"`
    - memUsage
    - memLimit
    - netIn
    - netOut
    - blockIn
    - blockOut
  - `[<1000]#,##0.00" KB";[<1000000]#,##0.00," MB";#,##0.00,," GB"`
    - mem
    - mem.free
    - heap.committed
    - heap.init
    - heap.used
    - heap
    - nonheap.committed
    - nonheap.init
    - nonheap.used
    - nonheap
3. 數值含意
  - jvm.mem = jvm.mem.free + jvm.heap.used + jvm.nonheap.used
  > https://github.com/spring-projects/spring-boot/blob/v1.3.2.RELEASE/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/endpoint/SystemPublicMetrics.java
