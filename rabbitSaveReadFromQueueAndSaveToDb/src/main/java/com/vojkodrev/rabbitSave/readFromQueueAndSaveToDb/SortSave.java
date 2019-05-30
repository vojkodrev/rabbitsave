package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

public class SortSave {
  final static Logger logger = Logger.getLogger(SortSave.class);

  private static final String EXCHANGE_NAME = "match_events";

  public static void main(String [] args)
  {

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

//      if (argv.length < 1) {
//        System.err.println("Usage: ReceiveLogsDirect [info] [warning] [error]");
//        System.exit(1);
//      }
//
//      for (String severity : argv) {
//        channel.queueBind(queueName, EXCHANGE_NAME, severity);
//      }

      channel.queueBind(queueName, EXCHANGE_NAME, rabbitmqQueueName);

      System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received '" +
          delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
      };
      
      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

    } catch (Throwable t) {
      logger.error(t.getMessage(), t);
    }

    logger.info("START!");

//    Observable
//      .create(new FileLineReader(args[0]))
//      .skip(1)
//      .flatMap(SortSaveRegexParser::new)
//      .take(10)
//      .flatMap(RabbitQueuer::new)
//      .subscribe(
//        item -> {
//        },
//        error -> {
//          logger.error(error.getMessage(), error);
//        },
//        () -> {
//          logger.info("DONE!");
//          System.exit(0);
//        }
//      );


  }
}

