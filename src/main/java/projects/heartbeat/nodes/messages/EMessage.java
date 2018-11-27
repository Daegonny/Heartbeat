package projects.heartbeat.nodes.messages;
import projects.heartbeat.models.TableEntry;
import sinalgo.nodes.messages.Message;
import projects.heartbeat.enums.MessageType;
import java.util.*;
import java.util.List;
import java.util.Arrays;

/* Description of Message Type */
public class EMessage extends Message {
  public long id, heartbeat;

  public EMessage(long id, long heartbeat) {
    this.id = id;
    this.heartbeat = heartbeat;
  }

  public Message clone() {
    return new EMessage(this.id, this.heartbeat);
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getHeartbeat(){
    return this.heartbeat;
  }

  public void setheartbeat(long heartbeat) {
    this.heartbeat = heartbeat;
  }
}
