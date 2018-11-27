package projects.heartbeat.models;
import java.util.*;
import java.util.List;
import java.util.Arrays;

public class TableEntry{
  private long id;
  private long heartbeat;
  private long timeStamp;

  public TableEntry(long id, long timeStamp, long heartbeat){
    this.id = id;
    this.heartbeat = heartbeat;
    this.timeStamp = timeStamp;
  }

  public long getId(){
    return this.id;
  }

  public long getTimeStamp(){
    return this.timeStamp;
  }

  public long getHeartbeat(){
    return this.heartbeat;
  }

  public void setTimeStamp(long timeStamp){
    this.timeStamp = timeStamp;
  }
}
