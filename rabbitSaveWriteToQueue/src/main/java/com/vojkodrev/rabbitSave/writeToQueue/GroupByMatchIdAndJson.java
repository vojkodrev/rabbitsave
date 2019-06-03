package com.vojkodrev.rabbitSave.writeToQueue;

import com.google.gson.Gson;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupByMatchIdAndJson implements ObservableSource<Map.Entry<Integer, String>> {

  private final List<Line> list;

  final static Logger logger = Logger.getLogger(RabbitQueuer.class);
  private final List<URI> rabbitmqServers;

  public GroupByMatchIdAndJson(List<Line> list, List<URI> rabbitmqServers) {

    this.list = list;
    this.rabbitmqServers = rabbitmqServers;
  }

  @Override
  public void subscribe(Observer<? super Map.Entry<Integer, String>> observer) {
    try {
      Map<Integer, List<Line>> collect = list.stream().collect(Collectors.groupingBy(i -> i.matchId % rabbitmqServers.size()));

      for (Map.Entry<Integer, List<Line>> item : collect.entrySet()) {
        Integer key = item.getKey();
        Object[] items = item.getValue().stream().map(i -> i.data).toArray();
        String json = new Gson().toJson(items);

        observer.onNext(new Map.Entry<Integer, String>() {
          @Override
          public Integer getKey() {
            return key;
          }

          @Override
          public String getValue() {
            return json;
          }

          @Override
          public String setValue(String value) {
            return null;
          }
        });
      }

      observer.onComplete();
    } catch (Throwable t) {
      observer.onError(t);
    }
  }

}
