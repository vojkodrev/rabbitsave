package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresDbSaver implements ObservableSource<List<Line>> {

  private final List<Line> list;

  final static Logger logger = Logger.getLogger(PostgresDbSaver.class);
  static MongoCollection<Document> collection;

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

      List<Document> documents = new ArrayList<>(list.size());

      for (int i = 0; i < list.size(); i++) {
        Line ssl = list.get(i);

        Document document = new Document("rabbitSaveEntryId", UUID.randomUUID().toString())
          .append("marketId", ssl.marketId)
          .append("matchId", ssl.matchId)
          .append("outcomeId", ssl.outcomeId)
          .append("receivedAt", ssl.receivedAt)
          .append("savedAtMs", System.currentTimeMillis())
          .append("savedAtNs", System.nanoTime())
          .append("specifiers", ssl.specifiers);

        documents.add(document);
      }

      collection.insertMany(documents);

      observer.onNext(list);
      observer.onComplete();

    } catch (Throwable t) {
      observer.onError(t);
    }

  }

  private void connect() {
    if (collection == null) {
      String mongoDbName = System.getenv("MONGODB_DB_NAME");
      String mongoDbCollection = System.getenv("MONGODB_COLLECTION");

      String connectionString = "mongodb://"
        + System.getenv("MONGODB_HOST")
        + ":" + System.getenv("MONGODB_PORT");

      logger.info("POSTGRESS CONNECTION STRING: " + connectionString);
      logger.info("MONGO DB NAME: " + mongoDbName);
      logger.info("MONGO DB COLLECTION: " + mongoDbCollection);

      MongoClient mongoClient = MongoClients.create(connectionString);
      MongoDatabase database = mongoClient.getDatabase(mongoDbName);
      collection = database.getCollection(mongoDbCollection);

      logger.info("CONNECTED TO MONGODB");
    }
  }
}
