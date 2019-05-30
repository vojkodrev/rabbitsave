package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import com.rabbitmq.client.*;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import org.apache.log4j.Logger;

public class RabbitDequeuer implements ObservableOnSubscribe<String> {


  final static Logger logger = Logger.getLogger(RabbitDequeuer.class);
  private static final String EXCHANGE_NAME = "match_events";

  public RabbitDequeuer() { }

  @Override
  public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {

    try {

      String rabbitmqHost = System.getenv("RABBITMQ_HOST");
      String rabbitmqPort = System.getenv("RABBITMQ_PORT");
      String rabbitmqQueueName = System.getenv("RABBITMQ_QUEUE");

      logger.info("RABBITMQ HOST: " + rabbitmqHost);
      logger.info("RABBITMQ PORT: " + rabbitmqPort);
      logger.info("RABBITMQ QUEUE: " + rabbitmqQueueName);

      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost(rabbitmqHost);
      factory.setPort(Integer.parseInt(rabbitmqPort));

      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();

      channel.exchangeDeclare(EXCHANGE_NAME, "direct");
      String queueName = channel.queueDeclare().getQueue();

      channel.queueBind(queueName, EXCHANGE_NAME, rabbitmqQueueName);

      logger.info(" [*] Waiting for messages. To exit press CTRL+C");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        logger.info(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

        observableEmitter.onNext(message);
      };

      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

    } catch (Throwable t) {
      observableEmitter.onError(t);
    }
  }
}
