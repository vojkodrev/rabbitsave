package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import com.google.gson.Gson;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;

public class RabbitMessageJsonParser implements ObservableSource<String> {


  final static Logger logger = Logger.getLogger(RabbitMessageJsonParser.class);
  private String message;

  public RabbitMessageJsonParser(String message) {

    this.message = message;
  }

  @Override
  public void subscribe(Observer<? super String> observer) {

    try {

      String[] strings = new Gson().fromJson(message, String[].class);
      for (String s : strings) {
        observer.onNext(s);
      }

      observer.onComplete();

    } catch (Throwable t) {
      observer.onError(t);
    }
  }
}
