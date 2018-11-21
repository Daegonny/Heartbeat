package projects.heartbeat.nodes.messages;
import projects.heartbeat.models.TableEntry;
import sinalgo.nodes.messages.Message;
import projects.heartbeat.enums.MessageType;
import java.util.*;
import java.util.List;
import java.util.Arrays;

/* Description of Message Type */
public class EMessage extends Message {
  public long id;
  public List<TableEntry> table;

  public EMessage(long id, List<TableEntry> table) {
    this.id = id;
    this.table = table;
  }

  public Message clone() {
    return new EMessage(id, table);
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<TableEntry> getTable() {
    return this.table;
  }

  public void setTable(List<TableEntry> table) {
    this.table = table;
  }
}
