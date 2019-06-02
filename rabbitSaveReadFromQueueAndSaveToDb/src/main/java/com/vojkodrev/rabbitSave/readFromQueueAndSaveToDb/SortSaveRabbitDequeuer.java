package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import org.apache.log4j.Logger;

public class SortSaveRabbitDequeuer implements ObservableOnSubscribe<String> {


  final static Logger logger = Logger.getLogger(SortSaveRabbitDequeuer.class);
  private static final String TASK_QUEUE_NAME = "task_queue";

  public SortSaveRabbitDequeuer() { }

  @Override
  public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {

    try {

      String rabbitmqHost = System.getenv("RABBITMQ_HOST");
      Integer rabbitmqPort = Integer.parseInt(System.getenv("RABBITMQ_PORT"));

      logger.info("RABBITMQ HOST: " + rabbitmqHost);
      logger.info("RABBITMQ PORT: " + rabbitmqPort);

      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost(rabbitmqHost);
      factory.setPort(rabbitmqPort);

      final Connection connection = factory.newConnection();
      final Channel channel = connection.createChannel();

      channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

      channel.basicQos(1);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        try {
          String message = new String(delivery.getBody(), "UTF-8");

          observableEmitter.onNext(message);
        } finally {
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
      };

      channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });

      logger.info("CONNECTED TO RABBITMQ");

    } catch (Throwable t) {
      observableEmitter.onError(t);
    }
  }

}



