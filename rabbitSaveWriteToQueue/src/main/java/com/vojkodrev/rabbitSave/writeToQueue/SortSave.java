package com.vojkodrev.rabbitSave.writeToQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SortSave {
  final static Logger logger = Logger.getLogger(SortSave.class);



  public static void main(String [] args)
  {

    // RABBITMQ_HOST=192.168.1.127;RABBITMQ_PORT=50002;RABBITMQ_QUEUE_COUNT=10


    logger.info("START!");

    Observable
      .create(new FileLineReader(args[0]))
      .skip(1)
      .flatMap(SortSaveRegexParser::new)
//      .take(10)
      .flatMap(RabbitQueuer::new)
      .subscribe(
        item -> {
        },
        error -> {
          logger.error(error.getMessage(), error);
        },
        () -> {
          logger.info("DONE!");

//          logger.info("STATS " + RabbitQueuer.statistics);
//
//          int sum = 0;
//          for (Map.Entry<String, Integer> item : RabbitQueuer.statistics.entrySet()) {
//            sum += item.getValue();
//          }
//
//          logger.info("SUM " + sum);

          System.exit(0);
        }
      );


  }
}

