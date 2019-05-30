package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import java.io.BufferedReader;
import java.io.FileReader;

public class FileLineReader implements ObservableOnSubscribe<String> {

  private final String filename;

  FileLineReader(String filename) {
    this.filename = filename;
  }

  @Override
  public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
    try {
      String line;
      BufferedReader bufferreader = new BufferedReader(new FileReader(filename));

      while ((line = bufferreader.readLine()) != null) {
        observableEmitter.onNext(line);
      }

      bufferreader.close();

      observableEmitter.onComplete();

    } catch (Throwable e) {
      observableEmitter.onError(e);
    }
  }
}
