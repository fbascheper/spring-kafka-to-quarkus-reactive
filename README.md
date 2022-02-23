# Sample traffic pipeline (Spring-Kafka version)

### Startup Docker containers

```shell
docker-compose up -d
docker volume ls
```

### Cleanup Docker containers with topics etc.

```shell
docker-compose down
docker volume prune --force
docker-compose up -d
docker volume ls

```

#### Test topics etc on broker using kafka-client commands
```shell
docker exec -it broker bash

export ZK_SVC=zookeeper:2181
export KAFKA_SVC=localhost:9092

# alternative: export KAFKA_SVC=broker:9092

kafka-topics --zookeeper ${ZK_SVC} --list

kafka-topics --zookeeper ${ZK_SVC} --delete --topic traffic-event 
kafka-topics --zookeeper ${ZK_SVC} --delete --topic vehicle-route-change-event  
kafka-topics --zookeeper ${ZK_SVC} --delete --topic vehicle-route-change-advice

kafka-topics --zookeeper ${ZK_SVC} --list

kafka-topics --zookeeper ${ZK_SVC} --create --topic traffic-event               --partitions 1 --replication-factor 1
kafka-topics --zookeeper ${ZK_SVC} --create --topic vehicle-route-change-event  --partitions 1 --replication-factor 1
kafka-topics --zookeeper ${ZK_SVC} --create --topic vehicle-route-change-advice --partitions 1 --replication-factor 1

kafka-run-class kafka.admin.ConsumerGroupCommand --bootstrap-server ${KAFKA_SVC} --list

# Show number of messages in topic
kafka-run-class kafka.admin.ConsumerGroupCommand --bootstrap-server ${KAFKA_SVC} --group cgRouteAdvice --describe


# No need to consume manually
# kafka-console-producer --bootstrap-server ${KAFKA_SVC} -topic test-topic 
# kafka-console-consumer --bootstrap-server ${KAFKA_SVC} -topic test-topic --from-beginning




```

### KSQL-DB cli

```shell

docker exec -it ksqldb-cli /bin/ksql -- http://ksqldb-server:8088

LIST TOPICS;

DROP STREAM TRAFFIC_EVENTS_STREAM;

CREATE OR REPLACE STREAM TRAFFIC_EVENTS_STREAM (
      sensorId VARCHAR
    , sensorAvailable BOOLEAN
    , vehicleClass VARCHAR
    , vehicleCount BIGINT
    , vehicleAverageSpeed BIGINT
    , vehicleHarmonicSpeed BIGINT
  ) WITH (
    KAFKA_TOPIC='traffic-event',
    VALUE_FORMAT='JSON'
  );



-- Look for sensors of hotspots ::: 

select 'hotspot', sensorId, vehicleHarmonicSpeed, vehicleCount 
  FROM  TRAFFIC_EVENTS_STREAM 
  WHERE vehicleHarmonicSpeed < 50
  AND   sensorAvailable = TRUE 
  AND   vehicleClass = 'CAR'
  AND   vehicleCount > 3
  AND   vehicleAverageSpeed < 200
  EMIT CHANGES;

-- Look for sensors of fast traffic  ::: 

select 'fast traffic', sensorId, vehicleHarmonicSpeed, vehicleCount 
  FROM  TRAFFIC_EVENTS_STREAM 
  WHERE vehicleHarmonicSpeed > 100
  AND   sensorAvailable = TRUE 
  AND   vehicleClass = 'CAR'
  AND   vehicleCount > 3
  AND   vehicleHarmonicSpeed < 200
  AND   vehicleAverageSpeed < 200
  EMIT CHANGES;

CREATE OR REPLACE STREAM VEHICLE_ROUTE_CHANGE_ADVICE_STREAM (
    vehicleId VARCHAR
    , suggestion VARCHAR
  ) WITH (
    KAFKA_TOPIC='vehicle-route-change-advice',
    VALUE_FORMAT='JSON'
  );

# show route change advice
select * from VEHICLE_ROUTE_CHANGE_ADVICE_STREAM EMIT CHANGES;
```
