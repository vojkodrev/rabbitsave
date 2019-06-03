package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PostgresDbSaver implements ObservableSource<List<Line>> {

  private final List<Line> list;

  final static Logger logger = Logger.getLogger(PostgresDbSaver.class);
  static Connection connection;
  static int saveCount;

  public PostgresDbSaver(List<Line> item) {
    this.list = item;
  }

  @Override
  public void subscribe(Observer<? super List<Line>> observer) {
    try {

      connect();

      if (list.isEmpty()) {
        observer.onNext(list);
        observer.onComplete();
        return;
      }

      StringBuilder query = new StringBuilder("INSERT INTO entry (marketid, matchid, outcomeid, receivedat, savedat, specifiers) VALUES ");

      for (int i = 0; i < list.size(); i++) {
        if (i > 0) {
          query.append(",");
        }

        query.append("(?,?,?,?,?,?)");
      }

      PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

      for (int i = 0; i < list.size(); i++) {
        Line ssl = list.get(i);
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
