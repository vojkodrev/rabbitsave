package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

public class Line {

  public long receivedAt;
  public int matchId;
  public int marketId;
  public String outcomeId;
  public String specifiers;

  public Line() {
  }

  public Line(int matchId, int marketId, String outcomeId, String specifiers, long receivedAt) {
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
