package MultiConsumer;
// To dynamically get the list of topics
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;

import java.util.Properties;
import java.util.Set;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;

public class MultiConsumer {
    public static void main(String[] args) {
      String server = "localhost:9092,localhost:9097,localhost:9100";
      String groupId = "Group2";
      try{
        Properties propertiesA = new Properties();
        propertiesA.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9097");
        Admin adminClient = Admin.create(propertiesA);
        ListTopicsResult ListTopicResult = adminClient.listTopics();
        Set<String> topics = ListTopicResult.names().get();
        
        List<Thread> consumerThreads =  new  ArrayList<>();
        List<ConsumerRunnable> consumerRunnables = new ArrayList<>();
        int thread_num = 1;
        for (String topic: topics){
          System.out.println(topic);
          ConsumerRunnable c = new ConsumerRunnable(server, groupId,topic,thread_num);
          consumerRunnables.add(c);
          Thread cT = new Thread(c);
          consumerThreads.add(cT);
          cT.start();
          thread_num += 1;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
          System.out.println("Shutdown Signal has resulting in the closing of all consumers");
          for (ConsumerRunnable c: consumerRunnables){
            c.shutdown();
          }
          for (Thread t: consumerThreads){
            try{
              t.join();
            }catch(Exception e){
              e.printStackTrace();
            }
          }
        }));

      }catch (Exception e){
        e.printStackTrace();
      }

      
    }
}

 
   
