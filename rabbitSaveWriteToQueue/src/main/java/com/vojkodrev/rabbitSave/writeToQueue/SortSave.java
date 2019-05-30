package com.vojkodrev.rabbitSave.writeToQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class SortSave {
  final static Logger logger = Logger.getLogger(SortSave.class);



  public static void main(String [] args)
  {


    logger.info("START!");

    Observable
      .create(new FileLineReader(args[0]))
      .skip(1)
      .flatMap(SortSaveRegexParser::new)
      .take(10)
      .flatMap(RabbitQueuer::new)
      .subscribe(
        item -> {
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

