package com.vojkodrev.rabbitSave.writeToQueue;

import com.rabbitmq.client.*;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;

import java.util.HashMap;

public class RabbitQueuer implements ObservableSource<SortSaveLine> {

  private final SortSaveLine item;

  final static Logger logger = Logger.getLogger(RabbitQueuer.class);
  private static final String EXCHANGE_NAME = "match_events";
  private static final String QUEUE_PREFIX = "queue_";
  private static int rabbitmqQueueCount;
  private static Channel channel;
  public static HashMap<String, Integer> statistics = new HashMap<>();

  public RabbitQueuer(SortSaveLine item) {
    this.item = item;
  }

  @Override
  public void subscribe(Observer<? super SortSaveLine> observer) {
    try {
      if (channel == null) {
        String rabbitmqHost = System.getenv("RABBITMQ_HOST");
        int rabbitmqPort = Integer.parseInt(System.getenv("RABBITMQ_PORT"));
        rabbitmqQueueCount = Integer.parseInt(System.getenv("RABBITMQ_QUEUE_COUNT"));

        logger.info("RABBITMQ HOST: " + rabbitmqHost);
        logger.info("RABBITMQ PORT: " + rabbitmqPort);
        logger.info("RABBITMQ QUEUE COUNT: " + rabbitmqQueueCount);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqHost);
        factory.setPort(rabbitmqPort);

        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);

        logger.info("CONNECTED TO RABBIT MQ");
      }

      String queue = QUEUE_PREFIX + (item.matchId % rabbitmqQueueCount);
      String message = item.data;

      channel.basicPublish(EXCHANGE_NAME, queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
//      logger.info(" [x] Sent '" + queue + "':'" + message + "'");

      if (!statistics.containsKey(queue)) {
        statistics.put(queue, 0);
      }

      statistics.put(queue, statistics.get(queue) + 1);

      observer.onNext(item);
      observer.onComplete();
    } catch (Throwable t) {
      observer.onError(t);
    }
  }
}
