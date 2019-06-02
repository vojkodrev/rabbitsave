package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class SortSave {
  final static Logger logger = Logger.getLogger(SortSave.class);

  public static void main(String [] args)
  {
    // RABBITMQ_HOST=192.168.1.127;RABBITMQ_PORT=50003;POSTGRES_HOST=192.168.1.127;POSTGRES_PORT=5432;POSTGRES_DB_NAME=rabbitsave;POSTGRES_USERNAME=postgres;POSTGRES_PASSWORD=rabbitsavepass;RX_BUFFER_SIZE=500;RX_BUFFER_TIME_LIMIT=1000

    int bufferSize = Integer.parseInt(System.getenv("RX_BUFFER_SIZE"));
    int bufferTimeLimit = Integer.parseInt(System.getenv("RX_BUFFER_TIME_LIMIT"));

    logger.info("RX BUFFER SIZE: " + bufferSize);
    logger.info("RX BUFFER TIME LIMIT: " + bufferTimeLimit);

    Observable
      .create(new SortSaveRabbitDequeuer())
      .flatMap(SortSaveRabbitJsonParser::new)
      .flatMap(SortSaveRegexParser::new)
      .buffer(bufferTimeLimit, TimeUnit.MILLISECONDS, bufferSize)
      .flatMap(SortSaveLineDbSaver::new)
      .subscribe(
        item -> {
        },
        error -> {
          logger.error(error.getMessage(), error);
        },
        () -> {
        }
      );


  }
}

