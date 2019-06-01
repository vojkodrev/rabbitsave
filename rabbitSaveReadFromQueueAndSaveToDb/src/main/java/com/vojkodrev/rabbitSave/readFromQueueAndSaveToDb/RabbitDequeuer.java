package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import com.rabbitmq.client.*;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class RabbitDequeuer implements FlowableOnSubscribe<String> {


  final static Logger logger = Logger.getLogger(RabbitDequeuer.class);
  private static final String EXCHANGE_NAME = "match_events";

  private static int receiveCount;

  public RabbitDequeuer() { }

  @Override
  public void subscribe(FlowableEmitter<String> flowableEmitter) throws Exception {

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

//      logger.info(" [*] Waiting for messages. To exit press CTRL+C");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        receiveCount++;

        String message = new String(delivery.getBody(), "UTF-8");
//        logger.info(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

        flowableEmitter.onNext(message);
      };

      new Timer().scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          logger.info("rabbit receive count " + receiveCount);
        }
      }, 2000, 2000);

      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

      logger.info("CONNECTED TO RABBITMQ");

    } catch (Throwable t) {
      flowableEmitter.onError(t);
    }
  }
}
