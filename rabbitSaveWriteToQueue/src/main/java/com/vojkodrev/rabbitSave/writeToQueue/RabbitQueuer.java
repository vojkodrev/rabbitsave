package com.vojkodrev.rabbitSave.writeToQueue;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RabbitQueuer implements ObservableSource<List<SortSaveLine>> {

  private final List<SortSaveLine> list;

  final static Logger logger = Logger.getLogger(RabbitQueuer.class);
  private static final String TASK_QUEUE_NAME = "task_queue";
  private static List<Channel> channels;
  public static HashMap<Integer, Integer> statistics = new HashMap<>();
  private static List<URI> rabbitmqServers;

  public RabbitQueuer(List<SortSaveLine> list) {
    this.list = list;
  }

  @Override
  public void subscribe(Observer<? super List<SortSaveLine>> observer) {
    try {

      connect();

      Map<Integer, List<SortSaveLine>> collect = list.stream().collect(Collectors.groupingBy(i -> i.matchId % rabbitmqServers.size()));

      for (Map.Entry<Integer, List<SortSaveLine>> item : collect.entrySet()) {
        Channel channel = channels.get(item.getKey());

        Object[] items = item.getValue().stream().map(i -> i.data).toArray();

        String json = new Gson().toJson(items);

        channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, json.getBytes("UTF-8"));

        if (!statistics.containsKey(item.getKey())) {
          statistics.put(item.getKey(), 0);
        }

        statistics.put(item.getKey(), statistics.get(item.getKey()) + item.getValue().size());
      }

      observer.onNext(list);
      observer.onComplete();
    } catch (Throwable t) {
      observer.onError(t);
    }
  }

  private static void connect() throws Throwable {
    if (channels == null) {
      channels = new ArrayList<>();

      rabbitmqServers = parseServers();
      logger.info("RABBITMQ SERVERS: " + rabbitmqServers);

      for (URI rabbitmqServer : rabbitmqServers) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqServer.getHost());
        factory.setPort(rabbitmqServer.getPort());

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channels.add(channel);
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
      }

      logger.info("CONNECTED TO RABBIT MQ");
    }
  }

  private static List<URI> parseServers() throws Throwable {
    String rabbitmqServersEnv = System.getenv("RABBITMQ_SERVERS");

    List<URI> rabbitmqServers = new ArrayList<>();

    for (String server : rabbitmqServersEnv.split(",")) {
      URI uri = new URI("rmq://" + server);
      rabbitmqServers.add(uri);
    }

    return rabbitmqServers;
  }
}
