# Temperature Anomaly Detector

Motivation: Temperaute anomaly detection of sourced temperature measurements from Kafka's topic.\
Features:
 - Anomaly raporting:
   - realtime raporting endpoint
   - data analitics endpoints
 - Anomaly detection alogorithms:
   - ALG_1, sequential temperature readings
   - ALG_2, time windowed temperature readings
 - Detection scope:
   - Global
   - Room
   - Device / Thermometer
   - Room and Device

### Component Stack Description
- Data Generator
  
  Utility tool used for population data records.

  For making it smoth and easy, I've used [doer](https://github.com/sygnowski/doer) tool.\
  Doer's manifests for data ingestion process located [here](data-gen/).

- Kafka Service
  
  Kafka Broker with Kafdrop UI

- Detector Application

  Spring Boot based app with rich REST API and data flows constructed in form of Kafka Streams Topology.

- MongoDB Server
  
  MongoDB server with Mongo Express UI.\
  Express UI creds: `admin:pass`

### All-in-One Docker Compose Deploymet

  For a demonstration porposes there is dedicated [docker-compose.yml](docker-compose.yml) file with all neccecary configuration.

  Compose file expose to localhost following ports for services / endpoints:
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
  # wait a while for initilization 
  # then, run othere components too
  docker compose up -d

  ```