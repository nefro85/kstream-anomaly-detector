# Anomaly Detector
Spring Boot based application with Kafka Stream Topology, used to emit and store 'temperature anomaly' readings
based on keyed processing.

Key aspects of application:
  - Stateful processing.
  - Usage of KTable with persisted state.
  - Time-Window based aggregation with state.
  - Real-time data visibility via REST API.

Motivation: Hands-on experience with KStream processing.

### Key Features:
 - Anomaly reporting into MongoDB:
   - Realtime reporting endpoint.
   - Data analytics endpoints.
 - Anomaly detection algorithms:
   - ALG_1, sequential temperature readings.
   - ALG_2, time windowed temperature readings.
 - Detection scope:
   - Global
   - Room
   - Device / Thermometer
   - Room and Device

### Canonical data model
```json
{
  "temperature": 23.4,
  "timestamp": 1711043278357,
  "roomId": "office",
  "thermometerId": "wall-device"
}
```

### Component Stack Description
- Data Generator
  
  Utility tool used for population data records.

  For making, it smooths and easy, I've used [doer](https://github.com/sygnowski/doer) tool.\
  Doer's manifest's for data ingestion process located [here](data-gen/).

- Kafka Service
  
  Kafka Broker with Kafdrop UI

- Detector Application

  Spring Boot based app with rich REST API and data flows constructed in form of Kafka Streams Topology.

- MongoDB Server
  
  MongoDB server with Mongo Express UI.\
  Express UI creds: `admin:pass`

### Configuration

  Anomaly Detection specific configuration via Spring configuration, [more details](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.external-config). 
  
  ```properties
  app.stream.tempMeasurementsTopic=temp.measurements
  app.stream.tempAnomalyTopic=temp.anomaly
  # anomaly scope selection [room, thermometer, both]
  app.stream.keyType=room
  app.stream.windowSize=PT30S # alg2, time window size.
  
  # anomaly calculation detection params 
  app.anomaly.meanSize=10
  app.anomaly.avgThreshold=9
  app.anomaly.deviationThreshold=5
  # anomaly analytics options
  app.anomaly.mostAnomalyThreshold=10
  # algorithm selection: alg1 - sequential, alg2 - 30 sec window
  app.anomaly.algName=alg1
  ```
### Real-Time API
  * Current readings: `curl -X 'GET' 'http://localhost:8080/realtime/readings'`
    ```json
    [
      {
        "index": "room-1",
        "detail": {
          "avg": 21.743000000000002,
          "data": [
            21.64,
            22.11,
            22.8,
            20.65,
            21.9,
            21.54,
            21.62,
            21.66,
            23.11,
            20.4
          ]
        }
      }
    ]
    ```
  * Anomalies: `curl -X 'GET' 'http://localhost:8080/realtime/anomalies'`
    ```json
    [
      {
        "temperature": 5.13,
        "timestamp": 1711203666227,
        "roomId": "room-1",
        "thermometerId": "dev-4"
      }
    ]
    ```
### All-in-One Docker Compose Deploymet

  For a demonstration purposes, there is a dedicated [docker-compose.yml](docker-compose.yml) file with all necessary configuration.

  Docker Compose will expose to localhost following ports for services / endpoints:
  | Service | Port |
  | ------- | ---- |
  | Kafdrop | [7000](http://localhost:7000) |
  | Swagger | [8080](http://localhost:8080/swagger-ui.html#/) |
  | Mongo Express UI | [8081](http://localhost:7000) |

  * Hints:

  ```bash
  # first execution steps
  # Kafka Broker
  docker compose up -d kafka
  # wait a while for initialization
  sleep 6 
  # then, run other components too
  docker compose up -d --build

  ```
  ```bash
  # cleanup
  docker compose down -v
  ```
  ```bash
  ./gradlew build
  docker compose up -d --build anomaly-detector 
  ```
