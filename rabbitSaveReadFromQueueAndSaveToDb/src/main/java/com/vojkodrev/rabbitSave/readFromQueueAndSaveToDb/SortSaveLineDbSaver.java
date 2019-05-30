//package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;
//
//import io.reactivex.ObservableSource;
//import io.reactivex.Observer;
//import org.apache.log4j.Logger;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.hibernate.cfg.AnnotationConfiguration;
//
//import java.util.List;
//
//public class SortSaveLineDbSaver implements ObservableSource<List<SortSaveLine>> {
//
//  private final List<SortSaveLine> list;
//
//  final static Logger logger = Logger.getLogger(SortSaveLineDbSaver.class);
//  final static SessionFactory sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
//  final static Session session = sessionFactory.openSession();
//
//  public SortSaveLineDbSaver(List<SortSaveLine> list) {
//    this.list = list;
//  }
//
//  @Override
//  public void subscribe(Observer<? super List<SortSaveLine>> observer) {
//
//    try {
//      logger.info("BEFORE DB SAVE");
//
//      Transaction tx = session.beginTransaction();
//      for (int i = 0; i < list.size(); i++) {
//        SortSaveLine ssl = list.get(i);
//        ssl.savedAt = System.currentTimeMillis();
//        session.save(ssl);
//      }
//      tx.commit();
//
//      logger.info("AFTER DB SAVE");
//
//      observer.onNext(list);
//      observer.onComplete();
//
//    } catch (Throwable t) {
//      observer.onError(t);
//    }
//
//  }
//}
