package com.vojkodrev.rabbitSave.readFromQueueAndSaveToDb;

public class SortSaveLine {

  public int matchId;
  public String data;

  public SortSaveLine() {
  }

  public SortSaveLine(int matchId, String data) {
    this.matchId = matchId;
    this.data = data;
  }

  @Override
  public String toString() {
    return String.format("Match Id = %d, Data = %s", matchId, data);
  }
}
