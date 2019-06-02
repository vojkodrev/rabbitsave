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
  private static final String TASK_QUEUE_NAME = "task_queue";

  private static int receiveCount;

  public RabbitDequeuer() { }

  @Override
  public void subscribe(FlowableEmitter<String> flowableEmitter) throws Exception {

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
//      System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

      channel.basicQos(1);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        receiveCount++;
        String message = new String(delivery.getBody(), "UTF-8");

//        System.out.println(" [x] Received '" + message + "'");
        try {
          flowableEmitter.onNext(message);
        } finally {
//          System.out.println(" [x] Done");
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
      };

      channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
        logger.info("consumer tag: " + consumerTag);
      });


      new Timer().scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          logger.info("rabbit receive count " + receiveCount);
        }
      }, 2000, 2000);



//      ConnectionFactory factory = new ConnectionFactory();
//      factory.setHost(rabbitmqHost);
//      factory.setPort(Integer.parseInt(rabbitmqPort));
//
//      Connection connection = factory.newConnection();
//      Channel channel = connection.createChannel();
//
//      channel.exchangeDeclare(EXCHANGE_NAME, "direct");
//      String queueName = channel.queueDeclare().getQueue();
//
//      channel.queueBind(queueName, EXCHANGE_NAME, rabbitmqQueueName);
//
////      logger.info(" [*] Waiting for messages. To exit press CTRL+C");
//
//      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//        receiveCount++;
//
//        String message = new String(delivery.getBody(), "UTF-8");
////        logger.info(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
//
//        flowableEmitter.onNext(message);
//      };
//
//      new Timer().scheduleAtFixedRate(new TimerTask() {
//        @Override
//        public void run() {
//          logger.info("rabbit receive count " + receiveCount);
//        }
//      }, 2000, 2000);
//
//      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

      logger.info("CONNECTED TO RABBITMQ");

    } catch (Throwable t) {
      flowableEmitter.onError(t);
    }
  }

//  private void doWork(String task) {
//    for (char ch : task.toCharArray()) {
//      if (ch == '.') {
//        try {
//          Thread.sleep(1000);
//        } catch (InterruptedException _ignored) {
//          Thread.currentThread().interrupt();
//        }
//      }
//    }
//  }
}
