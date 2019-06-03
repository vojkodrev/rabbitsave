package com.vojkodrev.rabbitSave.writeToQueue;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineRegexParser implements ObservableSource<Line> {

  private static final Pattern pattern = Pattern.compile("\\d+");
  private final String line;
  final static Logger logger = Logger.getLogger(LineRegexParser.class);

  public LineRegexParser(String line) {
    this.line = line;
  }

  @Override
  public void subscribe(Observer<? super Line> observer) {

//    logger.info("line parsed");
    Matcher matcher = pattern.matcher(this.line);

    if (!matcher.find()) {
      observer.onError(new Exception("Unable to parse \"" + this.line + "\"!"));
      return;
    }

    Line line = new Line(
      Integer.parseInt(matcher.group(0)),
      this.line + "|" + System.currentTimeMillis());

    observer.onNext(line);
    observer.onComplete();
  }
}

