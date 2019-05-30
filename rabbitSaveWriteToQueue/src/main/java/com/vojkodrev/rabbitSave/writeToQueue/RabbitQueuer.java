package com.vojkodrev.rabbitSave.writeToQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;

public class RabbitQueuer implements ObservableSource<SortSaveLine> {

  private final SortSaveLine item;

  final static Logger logger = Logger.getLogger(SortSave.class);
  private static final String EXCHANGE_NAME = "match_events";
  private static final String QUEUE_PREFIX = "queue_";
  private static final int QUEUE_COUNT = 10;
  private static Channel channel;

  public RabbitQueuer(SortSaveLine item) {
    this.item = item;
  }

  @Override
  public void subscribe(Observer<? super SortSaveLine> observer) {
    try {
      if (channel == null) {
        String rabbitmqHost = System.getenv("RABBITMQ_HOST");
        String rabbitmqPort = System.getenv("RABBITMQ_PORT");

        logger.info("RABBITMQ HOST: " + rabbitmqHost);
        logger.info("RABBITMQ PORT: " + rabbitmqPort);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqHost);
        factory.setPort(Integer.parseInt(rabbitmqPort));

        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
      }

      String queue = QUEUE_PREFIX + (item.matchId % QUEUE_COUNT);
      String message = item.data;

      channel.basicPublish(EXCHANGE_NAME, queue, null, message.getBytes("UTF-8"));
      logger.info(" [x] Sent '" + queue + "':'" + message + "'");

      observer.onNext(item);
      observer.onComplete();
    } catch (Throwable t) {
      observer.onError(t);
    }
  }
}
