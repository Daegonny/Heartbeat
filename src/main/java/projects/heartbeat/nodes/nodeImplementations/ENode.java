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
  private HashMap<Long, Long> heartbeatTable;
  private long heartbeat;
  private boolean working;

  public ENode() {
    super();
    this.heartbeat = 0;
    this.heartbeatTable = new HashMap<Long, Long>();
    this.working = true;
  }

  @Override
  public void handleMessages(Inbox inbox) {
    for (Message msg : inbox) {
      if (msg instanceof EMessage){
        EMessage emsg = (EMessage) msg;
        this.heartbeatTable.put(emsg.getId(), emsg.getHeartbeat());
      }
    }
  }

  public String toString() {
    return "Node " + this.getID();
  }

  @Override
  public void neighborhoodChange() {
    Connections nodeConnections = this.getOutgoingConnections();
    Set<Long> currentTableIds = this.heartbeatTable.keySet();

    for (Edge edge : nodeConnections) {
      ENode endNode = (ENode) edge.getEndNode();
      long id = endNode.getID();
      long heartbeat = endNode.getHeartbeat();
      if(this.heartbeatTable.containsKey(id)) {
        currentTableIds.remove(id); // Vai removendo, se sobra no fim, deu falha naqueles que sobraram.
      } else {
        this.heartbeatTable.put(id, heartbeat);
      }
    }

    if(currentTableIds.size() > 0) {
      Set<Long> setClone = new HashSet<Long>(currentTableIds);
      System.out.print("HOUVERAM FALHAS NOS SEGUINTES NÓS: ");
      Iterator it = setClone.iterator();
      while(it.hasNext()){
        long id = (long)it.next();
        System.out.print(id);
        this.heartbeatTable.remove(id);
      }
      System.out.println();
    }
  }

  public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
    String text = "" + this.getID();
    Color textColor = Color.WHITE;

    this.setColor(this.color);
    super.drawNodeAsDiskWithText(g, pt, highlight, text, 40, textColor);
  }

  @NodePopupMethod(menuText = "Print heartbeat")
  public void printheartbeat(){
    Tools.appendToOutput(String.format("Current heartbeat is %s.\n", this.heartbeat));
  }

  @NodePopupMethod(menuText = "Set failure")
  public void setFailure(){
    this.working = !this.working;
  }

  public void preStep() {
    this.heartbeat++;
    if(this.working){
      this.color = Color.BLUE;
      sendHeartbeats();
    } else {
      this.color = Color.PINK;
    }
      
    
  }

  public void init() {
  }

  public void postStep() {
    verifyFailures(); 
  }

  public void verifyFailures() {
    HashMap<Long, Long> tableClone = new HashMap<Long, Long>(this.heartbeatTable);
    Iterator it = tableClone.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      long id = (long)pair.getKey();
      long heartbeat = (long)pair.getValue();
      if(this.heartbeat > heartbeat + 1) { // O + 1 é gambiaraa, tem que resolver
        System.out.println("Nó " + this.getID() + ": HOUVE UMA FALHA NO NÓ " + id);
      }
    }
  }

  public void sendHeartbeats() {
    Connections nodeConnections = this.getOutgoingConnections();
    EMessage msg = new EMessage(this.getID(), this.heartbeat); 
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

  public long getHeartbeat() {
    return this.heartbeat;
  }

  public void setheartbeat(long heartbeat) {
    this.heartbeat = heartbeat;
  }
}
