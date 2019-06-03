package com.vojkodrev.rabbitSave.writeToQueue;

public class Line {

  public int matchId;
  public String data;

  public Line() {
  }

  public Line(int matchId, String data) {
    this.matchId = matchId;
    this.data = data;
  }

  @Override
  public String toString() {
    return String.format("Match Id = %d, Data = %s", matchId, data);
  }
}
