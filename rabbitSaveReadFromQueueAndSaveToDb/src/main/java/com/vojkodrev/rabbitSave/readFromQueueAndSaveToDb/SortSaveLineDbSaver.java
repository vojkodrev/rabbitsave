package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class SortSaveLineDbSaver implements ObservableSource<List<SortSaveLine>> {

  private final List<SortSaveLine> list;

  final static Logger logger = Logger.getLogger(SortSaveLineDbSaver.class);
  static Session session;

  public SortSaveLineDbSaver(List<SortSaveLine> item) {
    this.list = item;
  }

  @Override
  public void subscribe(Observer<? super List<SortSaveLine>> observer) {
    try {

      if (session == null) {

        Properties settings = new Properties();
        settings.put(Environment.DRIVER, "org.postgresql.Driver");

        settings.put(Environment.URL, "jdbc:postgresql://" +
          System.getenv("POSTGRES_HOST") + ":" +
          System.getenv("POSTGRES_PORT") + "/" +
          System.getenv("POSTGRES_DB_NAME"));

        settings.put(Environment.USER, System.getenv("POSTGRES_USERNAME"));
        settings.put(Environment.PASS, System.getenv("POSTGRES_PASSWORD"));
        settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        settings.put(Environment.SHOW_SQL, "false");
        settings.put(Environment.STATEMENT_BATCH_SIZE, System.getenv("POSTGRES_BATCH_SIZE"));
        settings.put(Environment.HBM2DDL_AUTO, "update");

        AnnotationConfiguration configuration = new AnnotationConfiguration();
        configuration.setProperties(settings);
        configuration.addAnnotatedClass(SortSaveLine.class);

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();

        logger.info("CONNECTED TO POSTGRES");

  //      logger.info("RUNNING TEST QUERY");
  //
  //      List list = session.createCriteria(SortSaveLine.class).list();
  //      logger.info("retrieved " + list.size() + " sort save lines");

      }

      if (list.isEmpty()) {
        observer.onComplete();
        return;
      }

//      logger.info("saving " + list.size() + " lines");
      Transaction tx = session.beginTransaction();
      for (int i = 0; i < list.size(); i++) {
        SortSaveLine ssl = list.get(i);
        ssl.savedAt = System.currentTimeMillis();
        ssl.id = UUID.randomUUID().toString();
        session.save(ssl);
      }
      tx.commit();


      observer.onNext(list);
      observer.onComplete();

    } catch (Throwable t) {
      observer.onError(t);
    }

  }
}
