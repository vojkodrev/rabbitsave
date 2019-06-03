package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineRegexParser implements ObservableSource<Line> {

  final static Logger logger = Logger.getLogger(LineRegexParser.class);
  private static final Pattern pattern = Pattern.compile("'.+?(\\d+?)'\\|(\\d+?)\\|'(.+?)'\\|('(.+?)')?\\|(\\d+)");
  private final String line;

  public LineRegexParser(String line) {
    this.line = line;
  }

  @Override
  public void subscribe(Observer<? super Line> observer) {

    Matcher matcher = pattern.matcher(this.line);

    if (!matcher.find()) {
      observer.onError(new Exception("Unable to parse \"" + this.line + "\"!"));
      return;
    }

    String specifiers = matcher.group(5);
    if (specifiers == null) {
      specifiers = "";
    }

    Line line = new Line(
      Integer.parseInt(matcher.group(1)),
      Integer.parseInt(matcher.group(2)),
      matcher.group(3),
      specifiers,
      Long.parseLong(matcher.group(6)));


    observer.onNext(line);
    observer.onComplete();
  }
}