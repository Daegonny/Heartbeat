package projects.heartbeat.models;
import java.util.*;
import java.util.List;
import java.util.Arrays;

public class TableEntry{
  private long id;
  private long timeStamp;
  private boolean flag;

  public TableEntry(long id, long timeStamp){
    this.id = id;
    this.timeStamp = timeStamp;
    this.flag = false;
  }

  public long getId(){
    return this.id;
  }

  public long getTimeStamp(){
    return this.timeStamp;
  }

  public boolean getFlag(){
    return this.flag;
  }

  public void setTimeStamp(long timeStamp){
    this.timeStamp = timeStamp;
  }

  public void mark(){
    this.flag = true;
  }

  public void unmark(){
    this.flag = false;
  }

}
