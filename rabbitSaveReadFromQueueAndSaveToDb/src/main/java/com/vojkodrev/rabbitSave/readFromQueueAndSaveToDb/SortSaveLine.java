package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SortSaveLine {

  @Id
  public String id;

  public long savedAt;
  public long receivedAt;

  public int matchId;
  public int marketId;
  public String outcomeId;
  public String specifiers;

  public SortSaveLine() {
  }

  public SortSaveLine(int matchId, int marketId, String outcomeId, String specifiers, long receivedAt) {
    this.matchId = matchId;
    this.marketId = marketId;
    this.outcomeId = outcomeId;
    this.specifiers = specifiers;
    this.receivedAt = receivedAt;
  }

  @Override
  public String toString() {
    return String.format("Match Id = %d, Market Id = %d, Outcome Id = %s, Specifiers = %s, ReceivedAt = %d",
      matchId, marketId, outcomeId, specifiers, receivedAt);
  }
}
