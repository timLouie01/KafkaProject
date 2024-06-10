package MultiConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.Duration;
import java.util.Collections;

public class ConsumerRunnable implements Runnable{
    private final KafkaConsumer<String, String> consumer;
    private final AtomicBoolean run = new AtomicBoolean(true);
    private final int thread_num;

    public ConsumerRunnable(String servers, String groupId, String topic,int thread_num){
        Properties propertiesC = new Properties();
        propertiesC.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        propertiesC.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        propertiesC.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        propertiesC.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        propertiesC.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumer = new KafkaConsumer<>(propertiesC);
        this.consumer.subscribe(Collections.singleton(topic));
        this.thread_num = thread_num;
    }

    public void run(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("Consumer" +this.thread_num+ "consumption.txt", true))){
            while (run.get()){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, String> record : records){
                    String message = "Consumed from Topic = " + record.topic() + " | Partition = " 
                                         + record.partition() + " | Key = " + record.key() + " | Value = " + record.value();
                    writer.write(message);
                    writer.newLine();
                }
            }
        }catch (WakeupException e) {
            if (this.run.get()) {
                throw e;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            this.consumer.close();
            System.out.println("Kafka consumer closed.");
        }
    }
    public void shutdown(){
        run.set(false);
        this.consumer.wakeup();
    }
    
}

 
   
