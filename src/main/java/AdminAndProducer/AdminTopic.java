package AdminAndProducer;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.List;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
public class AdminTopic
{
    private Admin adminClient = null;

    public Set<String> listTopics ( ){
        if (this.adminClient != null)
        {
            try{return this.adminClient.listTopics().names().get();}
            catch(Exception e){
                return new HashSet<>();
            }
            
        }
        else{
            return new HashSet<>();
        }

    }
    public static void main( String[] args )
    {
        Properties properties = new Properties();
        // Note that this Kafka client will "find" the rest of the servers automatically by itself
        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Using the new admin calss
        Admin adminClient = Admin.create(properties);
        try{
            DescribeClusterResult describeClusterResult = adminClient.describeCluster();
            int numOfBrokers = describeClusterResult.nodes().get().size();
            System.out.println("The number of Brokers: " + numOfBrokers);
            // Print out all the ports of the Brokers
            for (Node s:describeClusterResult.nodes().get()){
                System.out.println(s.port());
            }
            List<String> topics = new ArrayList<>();
            topics.add("Topic1");
            topics.add("Topic2");
            // List<NewTopic> newTopics = new ArrayList<>();
             try (BufferedWriter writer = new BufferedWriter(new FileWriter("partion_leaders.txt", false))) {
                for (String t: topics){
                    NewTopic topic = new NewTopic(t,numOfBrokers , (short) 2);
                    adminClient.createTopics(Collections.singleton(topic)).all().get();
                    try{
                        Thread.sleep(2000);
                    }catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                        System.out.println("The current thread was interrupted");
                    }
                    for (TopicDescription value : adminClient.describeTopics(Collections.singleton(t)).all().get().values()){
                        for (TopicPartitionInfo p : value.partitions()){
                            String message = value.name() + " Partition: " + p.partition() + "| Leader broker: " + p.leader().id();
                            writer.write(message);
                            writer.newLine();
                        }
                    }
                }
             }
             catch (Exception e){
                e.printStackTrace();
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
        

    }
}
