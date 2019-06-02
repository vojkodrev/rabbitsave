package com.vojkodrev.rabbitSave.writeToQueue;

import com.rabbitmq.client.*;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RabbitQueuer implements ObservableSource<SortSaveLine> {

  private final SortSaveLine item;

  final static Logger logger = Logger.getLogger(RabbitQueuer.class);
  private static final String TASK_QUEUE_NAME = "task_queue";
  private static List<Channel> channels;
  public static HashMap<Integer, Integer> statistics = new HashMap<>();

  public RabbitQueuer(SortSaveLine item) {
    this.item = item;
  }

  @Override
  public void subscribe(Observer<? super SortSaveLine> observer) {
    try {
//      logger.info("saving");

      if (channels == null) {
//        logger.info("creating channels");
        channels = new ArrayList<>();

        List<List<Object>> rabbitmqServers = parseServers();
        logger.info("RABBITMQ SERVERS: " + rabbitmqServers);

        for (List<Object> rabbitmqServer : rabbitmqServers) {
          ConnectionFactory factory = new ConnectionFactory();
          factory.setHost((String)rabbitmqServer.get(0));
          factory.setPort((Integer)rabbitmqServer.get(1));

          Connection connection = factory.newConnection();
          Channel channel = connection.createChannel();
          channels.add(channel);
          channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);



//          String message = String.join(" ", argv);

//          channel.basicPublish("", TASK_QUEUE_NAME,
//            MessageProperties.PERSISTENT_TEXT_PLAIN,
//            message.getBytes("UTF-8"));
//          System.out.println(" [x] Sent '" + message + "'");
        }

//        Thread.sleep(5000);

        logger.info("CONNECTED TO RABBIT MQ");
      }


//      if (channel == null) {
//        String rabbitmqHost = System.getenv("RABBITMQ_HOST");
//        int rabbitmqPort = Integer.parseInt(System.getenv("RABBITMQ_PORT"));
//        rabbitmqQueueCount = Integer.parseInt(System.getenv("RABBITMQ_QUEUE_COUNT"));
//
//        logger.info("RABBITMQ HOST: " + rabbitmqHost);
//        logger.info("RABBITMQ PORT: " + rabbitmqPort);
//        logger.info("RABBITMQ QUEUE COUNT: " + rabbitmqQueueCount);
//
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(rabbitmqHost);
//        factory.setPort(rabbitmqPort);
//
//        Connection connection = factory.newConnection();
//        channel = connection.createChannel();
//        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
//

//      }
//
//      String queue = QUEUE_PREFIX + (item.matchId % rabbitmqQueueCount);
//      String message = item.data;
//
//      channel.basicPublish(EXCHANGE_NAME, queue, null, message.getBytes("UTF-8"));
////      logger.info(" [x] Sent '" + queue + "':'" + message + "'");
//

      int channelNum = item.matchId % channels.size();
      Channel channel = channels.get(channelNum);

//      logger.info("line sent");
      channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, item.data.getBytes("UTF-8"));
//      Thread.sleep(10);

//      logger.info(" [x] Sent '" + item.data + "'");

      if (!statistics.containsKey(channelNum)) {
        statistics.put(channelNum, 0);
      }

      statistics.put(channelNum, statistics.get(channelNum) + 1);

      observer.onNext(item);
      observer.onComplete();
    } catch (Throwable t) {
      observer.onError(t);
    }
  }

  private List<List<Object>> parseServers() {
    String rabbitmqServersEnv = System.getenv("RABBITMQ_SERVERS");

    Pattern pattern = Pattern.compile("(.+?):(\\d+),?");
    Matcher matcher = pattern.matcher(rabbitmqServersEnv);
    List<List<Object>> rabbitmqServers = new ArrayList<>();
    matcher.results().iterator().forEachRemaining(i -> {
      ArrayList<Object> server = new ArrayList<>();
      server.add(i.group(1));
      server.add(Integer.parseInt(i.group(2)));
      rabbitmqServers.add(server);
    });

    return rabbitmqServers;
  }
}
