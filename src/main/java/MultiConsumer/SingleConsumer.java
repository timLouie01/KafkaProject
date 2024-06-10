package MultiConsumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.Duration;
import java.util.Collections;

public class SingleConsumer {
    private final KafkaConsumer<String, String> consumer;
    private final AtomicBoolean run = new AtomicBoolean(true);

    public SingleConsumer(String servers, String groupId, String topic) {
        Properties propertiesC = new Properties();
        propertiesC.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        propertiesC.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        propertiesC.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propertiesC.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propertiesC.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumer = new KafkaConsumer<>(propertiesC);
        this.consumer.subscribe(Collections.singleton(topic));
    }

    public static void main(String[] args) {
        SingleConsumer consumer1 = new SingleConsumer("localhost:9092,localhost:9097,localhost:9100", "Group1", "Topic1");

        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown signal received.");
            consumer1.shutdown();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));


        try (BufferedWriter writer = new BufferedWriter(new FileWriter("consumed_messages.txt", true))) {
            
                while (consumer1.run.get()) {
                    ConsumerRecords<String, String> records = consumer1.consumer.poll(Duration.ofMillis(200));
                    for (ConsumerRecord<String, String> record : records) {
                        String message = "Consumed from Topic = " + record.topic() + " | Partition = " 
                                         + record.partition() + " | Key = " + record.key() + " | Value = " + record.value();
                        writer.write(message);
                        writer.newLine();
                    }
                }
        } catch (WakeupException e) {
            if (consumer1.run.get()) {
                throw e;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            consumer1.consumer.close();
            System.out.println("Kafka consumer closed.");
        }
    }

    public void shutdown() {
        run.set(false);
        consumer.wakeup();
    }
}