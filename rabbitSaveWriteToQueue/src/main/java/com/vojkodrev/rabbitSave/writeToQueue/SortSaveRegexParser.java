package com.vojkodrev.rabbitSave.writeToQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortSaveRegexParser implements ObservableSource<SortSaveLine> {

  private static final Pattern pattern = Pattern.compile("\\d+");
  private final String line;

  public SortSaveRegexParser(String line) {
    this.line = line;
  }

  @Override
  public void subscribe(Observer<? super SortSaveLine> observer) {

    Matcher matcher = pattern.matcher(line);

    if (!matcher.find()) {
      observer.onError(new Exception("Unable to parse \"" + line + "\"!"));
      return;
    }

    SortSaveLine sortSaveLine = new SortSaveLine(
      Integer.parseInt(matcher.group(0)),
      line);

    observer.onNext(sortSaveLine);
    observer.onComplete();
  }
}

