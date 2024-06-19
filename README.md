# Exploration of Kafka's Client APIs

- This java project interacts with the Apache Kafka Producer, Consumer, and Admin APIs. It runs Kafka in KRaft mode and utilizes docker containers.
- It can be built running **mvn clean install** followed by running **docker compose up -d** from the project root directory which starts the kafka cluster.
- The kafka cluster can be configured and customized(adding more/less brokers) in the **compose.yaml** file.
- The AdminTopic writes the leader broker for each partition to the file **partion_leaders.txt**
- The SingleConsumer writes to the file **consumed_messages.txt** while the MultiConsumers write to the files **Consumer1consumption.txt** and **Consumer2consumption.txt**.
- *Note that by the **ConsumerConfig.AUTO_OFFSET_RESET_CONFIG** both the single consumers and multi consumers read from the "beginning" each time to support easier testing.
- *Note that by running **docker compose pause** and **docker compose unpause** you can pause the kafka brokers during testing.
- *Note that by running **docker compose down -v** to shutdown the kafka brokers and delete persisted volumes to start fresh.
