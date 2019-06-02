package com.vojkodrev.rabbitSave.writeToQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class SortSave {
  final static Logger logger = Logger.getLogger(SortSave.class);



  public static void main(String [] args)
  {

    // RX_BUFFER_SIZE=1000;RX_BUFFER_TIME_LIMIT=250;RABBITMQ_SERVERS=192.168.1.127:50000,192.168.1.127:50001,192.168.1.127:50002,192.168.1.127:50003,192.168.1.127:50004,192.168.1.127:50005,192.168.1.127:50006,192.168.1.127:50007,192.168.1.127:50008,192.168.1.127:50009;INPUT_FILE=fo_random.txt

    int bufferSize = Integer.parseInt(System.getenv("RX_BUFFER_SIZE"));
    int bufferTimeLimit = Integer.parseInt(System.getenv("RX_BUFFER_TIME_LIMIT"));

    logger.info("RX BUFFER SIZE: " + bufferSize);
    logger.info("RX BUFFER TIME LIMIT: " + bufferTimeLimit);

    String inputFile = System.getenv("INPUT_FILE");

    logger.info("INPUT FILE: " + inputFile);

    Observable
      .create(new FileLineReader(inputFile))
      .skip(1)
      .flatMap(SortSaveRegexParser::new)
      .buffer(bufferTimeLimit, TimeUnit.MILLISECONDS, bufferSize)
      .flatMap(RabbitQueuer::new)
      .subscribe(
        item -> {
        },
        error -> {
          logger.error(error.getMessage(), error);
        },
        () -> {
          logger.info("DONE!");

          logger.info("STATS " + RabbitQueuer.statistics);

          int sum = 0;
          for (Map.Entry<Integer, Integer> item : RabbitQueuer.statistics.entrySet()) {
            sum += item.getValue();
          }

          logger.info("SUM " + sum);

          System.exit(0);
        }
      );


  }
}

