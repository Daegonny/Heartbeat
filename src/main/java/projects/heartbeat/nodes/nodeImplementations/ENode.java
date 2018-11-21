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
  private List<TableEntry> table;
  private ENode successor;
  private long timeStamp;
  private final long T = 10;

  public ENode() {
    super();
    this.timeStamp = 0;
  }

  @Override
  public void handleMessages(Inbox inbox) {
    for (Message msg : inbox) {
      if (msg instanceof EMessage){
        EMessage emsg = (EMessage) msg;
        mergeTables(emsg.getTable());
      }
    }
  }

  public void mergeTables(List<TableEntry> table){

  }

  public String toString() {
    return "Node " + this.getID();
  }

  @Override
  public void neighborhoodChange() {
    Connections nodeConnections = this.getOutgoingConnections();
    ENode firstConnectionNode = (ENode) nodeConnections.iterator().next().getEndNode();
    successor = null;

    for (Edge edge : nodeConnections) {
      ENode endNode = (ENode) edge.getEndNode();
      if (endNode.compareTo(this) > 0) {
        if (successor == null)
          successor = endNode;
        else
          successor = endNode.compareTo(successor) < 0 ? endNode : successor;
      }
    }

    if (successor == null) { // Last Node
      successor = firstConnectionNode;
      System.out.println(String.format("Last node %d.", this.getID()));
      for (Edge edge : nodeConnections) { // Find the smallest
        ENode endNode = (ENode) edge.getEndNode();
        successor = endNode.compareTo(successor) < 0 ? endNode : successor;
      }
    }
    System.out.print(String.format("Sucessor of %s is %s", this, successor));
  }

  public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
    String text = "" + this.getID();
    Color textColor = Color.WHITE;

    this.setColor(this.color);
    super.drawNodeAsDiskWithText(g, pt, highlight, text, 70, textColor);
  }

  @NodePopupMethod(menuText = "Print timeStamp")
  public void printTimeStamp(){
    Tools.appendToOutput(String.format("Current timeStamp is %s \n", this.timeStamp));
  }

  // @NodePopupMethod(menuText = "Start Election")
  // public void startElection() {
  //   EMessage msg = new EMessage(this.getID(), MessageType.ELECTION);
  //   ETimer timer = new ETimer(this, successor, 1);
  //
  //   timer.startRelative(1, this);
  //   Tools.appendToOutput(String.format("Start Routing from %s \n", this));
  // }

  public void preStep() {
  }

  public void init() {
  }

  public void postStep() {
    this.timeStamp ++;
  }

  public void checkRequirements() {
  }

  public void compute() {
  }
}
