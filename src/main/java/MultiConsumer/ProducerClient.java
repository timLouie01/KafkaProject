package MultiConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
// To dynamically get the list of topics
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.Set;
import java.util.List;
public class ProducerClient
{
    private static class ErrorHandle implements Callback{
        public void onCompletion(RecordMetadata recordMeta, Exception e){
           
            if (e != null){
                System.err.print(recordMeta.toString());
            }
            else{System.err.print("RECORD SENT");}
        }
    }
    public static void main( String[] args )
    {
       
        Properties propertiesP = new Properties();
        propertiesP.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9097");
        propertiesP.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        propertiesP.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(propertiesP);
        try{
            Properties propertiesA = new Properties();
        propertiesA.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        Admin adminClient = Admin.create(propertiesA);
        ListTopicsResult ListTopicResult = adminClient.listTopics();
        Set<String> topics = ListTopicResult.names().get();
        for (String topic : topics){
            List<PartitionInfo> partitions = producer.partitionsFor(topic);
            for (PartitionInfo part : partitions){
                // To each Topic's partition send the record of: name_of_topic:partition_number
                ProducerRecord<String, String> record = new ProducerRecord<String,String>(topic,part.partition(),"Tim",topic+":"+part.partition());
                try {
                // Synchronousy message send
                producer.send(record, new ErrorHandle()).get();

                }catch (Exception e){
                e.printStackTrace();
                }
            }
            
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        

            
           
        
    }
}
