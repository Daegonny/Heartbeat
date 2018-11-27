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
  private HashMap<Long, TableEntry> table;
  private long heartbeat;
  private long timeStamp;
  private boolean working;
  private int t;

  public ENode() {
    super();
    this.heartbeat = 0;
    this.timeStamp = 0;
    this.table = new HashMap<Long, TableEntry>();
    this.working = true;
    this.t = 3;
  }

  @Override
  public void handleMessages(Inbox inbox) {
    if(this.working){
      for (Message msg : inbox) {
        if (msg instanceof EMessage){
          EMessage emsg = (EMessage) msg;
          TableEntry tableEntry = (TableEntry)this.table.get(emsg.getId());
          if (tableEntry != null){
            long entryHeartbeat = tableEntry.getHeartbeat();
            if(emsg.getHeartbeat() > entryHeartbeat){//nova mensagem
              this.table.put(emsg.getId(), new TableEntry(emsg.getId(), this.timeStamp, emsg.getHeartbeat()));
            }
          }
          else{
            this.table.put(emsg.getId(), new TableEntry(emsg.getId(), this.timeStamp, emsg.getHeartbeat()));
          }
        }
      }
    }
  }

  public String toString() {
    return "Node " + this.getID();
  }

  @Override
  public void neighborhoodChange() {
    Connections nodeConnections = this.getOutgoingConnections();
  }

  public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
    String text = "" + this.getID();
    Color textColor = Color.WHITE;

    this.setColor(this.color);
    super.drawNodeAsDiskWithText(g, pt, highlight, text, 40, textColor);
  }

  @NodePopupMethod(menuText = "Print heartbeat")
  public void printheartbeat(){
    System.out.println("-----");
    System.out.println("heartbeat do Nó " + this.getID() + ": " + this.heartbeat);
  }

  @NodePopupMethod(menuText = "Print timeStamp")
  public void printTimeStamp(){
    System.out.println("-----");
    System.out.println("timeStamp do Nó " + this.getID() + ": " + this.timeStamp);
  }

  @NodePopupMethod(menuText = "Show table")
  public void showTable(){
    HashMap<Long, TableEntry> tableClone = new HashMap<Long, TableEntry>(this.table);
    Iterator it = tableClone.entrySet().iterator();
    if(tableClone.size() < 1){
      System.out.println("The table is empty");
    }
    else{
      System.out.println("-----");
      System.out.println("Tabela do Nó: "+this.getID());
      while(it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        TableEntry tableEntry = (TableEntry)pair.getValue();
        long entryId = (long)pair.getKey();
        System.out.println("id: " + entryId + " | heartbeat: " + tableEntry.getHeartbeat() + " | timeStamp: "+tableEntry.getTimeStamp());
      }
    }
  }

  @NodePopupMethod(menuText = "Switch Working")
  public void switchWorking(){
    this.working = !this.working;
  }

  public void preStep() {
    if(this.working){
      this.heartbeat++;
      this.timeStamp++;
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
    HashMap<Long, TableEntry> tableClone = new HashMap<Long, TableEntry>(this.table);
    Iterator it = tableClone.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      TableEntry tableEntry = (TableEntry)pair.getValue();
      long entryId = (long)pair.getKey();

      if(this.timeStamp - tableEntry.getTimeStamp() > t ){
        System.out.println("-----");
        System.out.println("Nó " + this.getID() + ": Falha detectada no nó: " + entryId);
        this.table.remove(entryId);
      }
    }
  }

  public void sendHeartbeats() {
    if(working){
      Connections nodeConnections = this.getOutgoingConnections();
      EMessage msg = new EMessage(this.getID(), this.heartbeat);
      if(nodeConnections != null){
        for (Edge edge : nodeConnections) {
          this.send(msg, edge.getEndNode());
        }
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
