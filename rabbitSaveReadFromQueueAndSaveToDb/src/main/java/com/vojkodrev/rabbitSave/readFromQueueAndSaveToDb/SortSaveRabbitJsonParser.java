package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import com.google.gson.Gson;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class SortSaveRabbitJsonParser implements ObservableSource<String> {


  final static Logger logger = Logger.getLogger(SortSaveRabbitJsonParser.class);
  private String message;

  public SortSaveRabbitJsonParser(String message) {

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
