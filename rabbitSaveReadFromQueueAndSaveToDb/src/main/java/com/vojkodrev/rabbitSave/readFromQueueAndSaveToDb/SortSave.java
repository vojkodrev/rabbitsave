package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

public class SortSave {
  final static Logger logger = Logger.getLogger(SortSave.class);

  public static void main(String [] args)
  {
    logger.info("START!");

    Observable
      .create(new RabbitDequeuer())
      .flatMap(SortSaveRegexParser::new)
//      .take(10)
//      .flatMap(RabbitQueuer::new)
      .subscribe(
        item -> {
          logger.info(item);
        },
        error -> {
          logger.error(error.getMessage(), error);
        },
        () -> {
          logger.info("DONE!");
          System.exit(0);
        }
      );


  }
}

