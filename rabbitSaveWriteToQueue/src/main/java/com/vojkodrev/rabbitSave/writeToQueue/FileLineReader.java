package com.vojkodrev.rabbitSave.writeToQueue;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileLineReader implements ObservableOnSubscribe<String> {

  private final String filename;
  final static Logger logger = Logger.getLogger(FileLineReader.class);

  FileLineReader(String filename) {
    this.filename = filename;
  }

  @Override
  public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
    try {
//      logger.info("reading file");
      String line;
      BufferedReader bufferreader = new BufferedReader(new FileReader(filename));

      while ((line = bufferreader.readLine()) != null) {
//        logger.info("line read");
        observableEmitter.onNext(line);
      }

      bufferreader.close();

      observableEmitter.onComplete();

    } catch (Throwable e) {
      observableEmitter.onError(e);
    }
  }
}
