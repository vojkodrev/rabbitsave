package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class SortSave {
  final static Logger logger = Logger.getLogger(SortSave.class);

  public static void main(String [] args)
  {
    // RABBITMQ_HOST=192.168.1.127;RABBITMQ_PORT=50003;RABBITMQ_QUEUE=queue_1;POSTGRES_HOST=192.168.1.127;POSTGRES_PORT=5432;POSTGRES_DB_NAME=rabbitsave;POSTGRES_USERNAME=postgres;POSTGRES_PASSWORD=rabbitsavepass;POSTGRES_BATCH_SIZE=50;OBSERVABLE_BUFFER_SIZE=500;OBSERVABLE_BUFFER_TIME_LIMIT=2000

    int bufferSize = Integer.parseInt(System.getenv("OBSERVABLE_BUFFER_SIZE"));
    int bufferTimeLimit = Integer.parseInt(System.getenv("OBSERVABLE_BUFFER_TIME_LIMIT"));

    logger.info("OBSERVABLE BUFFER SIZE: " + bufferSize);
    logger.info("OBSERVABLE BUFFER TIME LIMIT: " + bufferTimeLimit);
    logger.info("START!");

    Observable
      .create(new RabbitDequeuer())
      .flatMap(SortSaveRegexParser::new)
      .buffer(bufferTimeLimit, TimeUnit.MILLISECONDS, bufferSize)
      .flatMap(SortSaveLineDbSaver::new)
//      .take(10)
//      .flatMap(RabbitQueuer::new)
      .subscribe(
        item -> {
//          logger.info(item);
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

