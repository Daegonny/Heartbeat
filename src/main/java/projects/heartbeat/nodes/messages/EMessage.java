package projects.heartbeat.nodes.messages;
import projects.heartbeat.models.TableEntry;
import sinalgo.nodes.messages.Message;
import projects.heartbeat.enums.MessageType;
import java.util.*;
import java.util.List;
import java.util.Arrays;

/* Description of Message Type */
public class EMessage extends Message {
  public long id, timestamp;

  public EMessage(long id, long timestamp) {
    this.id = id;
    this.timestamp = timestamp;
  }

  public Message clone() {
    return new EMessage(this.id, this.timestamp);
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getTimestamp(){
    return this.timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
