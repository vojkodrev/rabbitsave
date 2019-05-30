package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SortSaveLine {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  public int id;

  public long savedAt;

  public int matchId;
  public int marketId;
  public String outcomeId;
  public String specifiers;

  public SortSaveLine() {
  }

  public SortSaveLine(int matchId, int marketId, String outcomeId, String specifiers) {
    this.matchId = matchId;
    this.marketId = marketId;
    this.outcomeId = outcomeId;
    this.specifiers = specifiers;
  }

  @Override
  public String toString() {
    return String.format("Match Id = %d, Market Id = %d, Outcome Id = %s, Specifiers = %s", matchId, marketId, outcomeId, specifiers);
  }
}
