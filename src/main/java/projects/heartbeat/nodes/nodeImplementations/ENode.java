package projects.heartbeat.nodes.nodeImplementations;
import java.awt.Color;
import java.awt.Graphics;
import java.util.*;
import java.util.List;
import java.util.Arrays;
import projects.defaultProject.nodes.timers.MessageTimer;
import sinalgo.tools.Tools;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import projects.heartbeat.nodes.timers.ETimer;
import projects.heartbeat.nodes.messages.EMessage;
import projects.heartbeat.enums.MessageType;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.logging.Logging;
import sinalgo.nodes.Connections;
import sinalgo.nodes.edges.Edge;
import projects.heartbeat.models.TableEntry;

public class ENode extends Node {

  private Color color = Color.BLUE;
  private HashMap<Long, Long> timestampTable;
  private long timestamp;

  public ENode() {
    super();
    this.timestamp = 0;
    this.timestampTable = new HashMap<Long, Long>();
  }

  @Override
  public void handleMessages(Inbox inbox) {
    for (Message msg : inbox) {
      if (msg instanceof EMessage){
        EMessage emsg = (EMessage) msg;
        this.timestampTable.put(emsg.getId(), emsg.getTimestamp());
      }
    }
  }

  public String toString() {
    return "Node " + this.getID();
  }

  @Override
  public void neighborhoodChange() {
    Connections nodeConnections = this.getOutgoingConnections();
    Set<Long> currentTableIds = this.timestampTable.keySet();

    for (Edge edge : nodeConnections) {
      ENode endNode = (ENode) edge.getEndNode();
      long id = endNode.getID();
      long timestamp = endNode.getTimestamp();
      if(this.timestampTable.containsKey(id)) {
        currentTableIds.remove(id); // Vai removendo, se sobra no fim, deu falha naqueles que sobraram.
      } else {
        this.timestampTable.put(id, timestamp);
      }
    }

    if(currentTableIds.size() > 0) {
      Set<Long> setClone = new HashSet<Long>(currentTableIds);
      System.out.print("HOUVERAM FALHAS NOS SEGUINTES NÓS: ");
      Iterator it = setClone.iterator();
      while(it.hasNext()){
        long id = (long)it.next();
        System.out.print(id);
        this.timestampTable.remove(id);
      }
      System.out.println();
      // TODO(@andre): Notificar uma falha na tela de verdade
    }
  }

  public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
    String text = "" + this.getID();
    Color textColor = Color.WHITE;

    this.setColor(this.color);
    super.drawNodeAsDiskWithText(g, pt, highlight, text, 70, textColor);
  }

  @NodePopupMethod(menuText = "Print timeStamp")
  public void printTimeStamp(){
    Tools.appendToOutput(String.format("Current timeStamp is %s \n", this.timestamp));
  }

  public void preStep() {
    this.timestamp++;
    sendHeartbeats();
  }

  public void init() {
  }

  public void postStep() {
    verifyFailures();
  }

  public void verifyFailures() {
    HashMap<Long, Long> tableClone = new HashMap<Long, Long>(this.timestampTable);
    Iterator it = tableClone.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      long id = (long)pair.getKey();
      long timestamp = (long)pair.getValue();
      if(this.timestamp > timestamp + 1) { // O + 1 é gambiaraa, tem que resolver
        System.out.println("HOUVE UMA FALHA NO NÓ " + id);
        this.timestampTable.remove(id);
        // TODO(@andre): Notificar uma falha na tela de verdade
      }
    }
  }

  public void sendHeartbeats() {
    Connections nodeConnections = this.getOutgoingConnections();
    EMessage msg = new EMessage(this.getID(), this.timestamp); 
    if(nodeConnections != null){
      for (Edge edge : nodeConnections) {
        this.send(msg, edge.getEndNode());
      }
    }
  }

  public void checkRequirements() {
  }

  public void compute() {
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
