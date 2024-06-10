# KafkaProject

- This java project interacts with the Apache Kafka Producer, Consumer, and Admin APIs. It runs Kafka in KRaft mode and utilizes docker containers.
- It can be built running **mvn clean install** followed by running **docker compose up -d** from the project root directory which starts the kafka cluster.
- The kafka cluster can be configured and customized(adding more/less brokers) in the **compose.yaml** file.
- The SingleConsumer writes to the file **consumed_messages.txt** while the MultiConsumers write to the files **Consumer1consumption.txt** and **Consumer2consumption.txt**.
