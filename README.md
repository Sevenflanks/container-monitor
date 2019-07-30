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

- [ ] run as helm chart

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
