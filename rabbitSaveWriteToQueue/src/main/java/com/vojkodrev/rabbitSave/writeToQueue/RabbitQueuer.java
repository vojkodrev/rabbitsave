package com.vojkodrev.rabbitSave.writeToQueue;

import com.rabbitmq.client.*;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;
import java.net.URI;
import java.util.*;

public class RabbitQueuer implements ObservableSource<Map.Entry<Integer, String>> {

  private final Map.Entry<Integer, String> entry;
  private List<URI> rabbitmqServers;

  final static Logger logger = Logger.getLogger(RabbitQueuer.class);
  private static final String TASK_QUEUE_NAME = "task_queue";
  private static List<Channel> channels;


  public RabbitQueuer(Map.Entry<Integer, String> entry, List<URI> rabbitmqServers) {

    this.entry = entry;
    this.rabbitmqServers = rabbitmqServers;
  }

  @Override
  public void subscribe(Observer<? super Map.Entry<Integer, String>> observer) {
    try {

      connect();

      Channel channel = channels.get(entry.getKey());
      channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, entry.getValue().getBytes("UTF-8"));

      observer.onNext(entry);
      observer.onComplete();
    } catch (Throwable t) {
      observer.onError(t);
    }
  }

  private void connect() throws Throwable {
    if (channels == null) {
      channels = new ArrayList<>();

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


}

