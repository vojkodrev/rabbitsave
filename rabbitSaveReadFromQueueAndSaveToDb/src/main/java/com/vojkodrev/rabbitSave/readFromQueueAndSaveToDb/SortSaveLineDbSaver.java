package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class SortSaveLineDbSaver implements ObservableSource<List<SortSaveLine>> {

  private final List<SortSaveLine> list;

  final static Logger logger = Logger.getLogger(SortSaveLineDbSaver.class);
  static Connection connection;
  static int saveCount;

  public SortSaveLineDbSaver(List<SortSaveLine> item) {
    this.list = item;
  }

  @Override
  public void subscribe(Observer<? super List<SortSaveLine>> observer) {
    try {

      connect();

      if (list.isEmpty()) {
        observer.onNext(list);
        observer.onComplete();
        return;
      }

      StringBuilder query = new StringBuilder("INSERT INTO sortsaveline (marketid, matchid, outcomeid, receivedat, savedat, specifiers) VALUES ");

      for (int i = 0; i < list.size(); i++) {
        if (i > 0) {
          query.append(",");
        }

        query.append("(?,?,?,?,?,?)");
      }

      PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

      for (int i = 0; i < list.size(); i++) {
        SortSaveLine ssl = list.get(i);
        preparedStatement.setInt(1 + 6 * i, ssl.marketId);
        preparedStatement.setInt(2 + 6 * i, ssl.matchId);
        preparedStatement.setString(3 + 6 * i, ssl.outcomeId);
        preparedStatement.setLong(4 + 6 * i, ssl.receivedAt);
        preparedStatement.setLong(5 + 6 * i, System.currentTimeMillis());
        preparedStatement.setString(6 + 6 * i, ssl.specifiers);
      }

      preparedStatement.executeUpdate();

      observer.onNext(list);
      observer.onComplete();

    } catch (Throwable t) {
      observer.onError(t);
    }

  }

  private void connect() throws SQLException {
    if (connection == null) {
      String username = System.getenv("POSTGRES_USERNAME");

      String connectionString = "jdbc:postgresql://"
        + System.getenv("POSTGRES_HOST")
        + ":" + System.getenv("POSTGRES_PORT")
        + "/" + System.getenv("POSTGRES_DB_NAME");

      logger.info("POSTGRESS CONNECTION STRING: " + connectionString);
      logger.info("POSTGRESS USERNAME: " + username);

      connection = DriverManager.getConnection(connectionString, username, System.getenv("POSTGRES_PASSWORD"));

      logger.info("CONNECTED TO POSTGRES");
    }
  }
}
