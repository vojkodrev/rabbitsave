//package com.vojkodrev.rabbitSave.writeToQueue;
//
//import io.reactivex.ObservableSource;
//import io.reactivex.Observer;
//import org.apache.log4j.Logger;
//
//import java.util.Collections;
//import java.util.List;
//
//public class SortSaveLineSorter implements ObservableSource<List<SortSaveLine>> {
//
//  private final List<SortSaveLine> list;
//  final static Logger logger = Logger.getLogger(SortSaveLineSorter.class);
//
//  public SortSaveLineSorter(List<SortSaveLine> list) {
//    this.list = list;
//  }
//
//  @Override
//  public void subscribe(Observer<? super List<SortSaveLine>> observer) {
//
//    logger.info("BEFORE SORT");
//
//    Collections.sort(list, (o1, o2) -> {
//      int matchIdD = o1.matchId - o2.matchId;
//      if (matchIdD != 0) {
//        return matchIdD;
//      }
//
//      int marketIdD = o1.marketId - o2.marketId;
//      if (marketIdD != 0) {
//        return marketIdD;
//      }
//
//      int outcomeIdD = o1.outcomeId.compareTo(o2.outcomeId);
//      if (outcomeIdD != 0) {
//        return outcomeIdD;
//      }
//
//      return (o1.specifiers).compareTo(o2.specifiers);
//    });
//
//    logger.info("AFTER SORT");
//
//    observer.onNext(list);
//    observer.onComplete();
//  }
//}
//
