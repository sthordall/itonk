package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.Stack;

/**
*  Implementation of NodeRemoteInterface
*/
public class Node implements NodeRemoteInterface {

  public Integer NODE_ID = 0;
  public Integer LEADER_ID = 0;

  public Boolean isLeader;
  private Registry registry;
  private Node leader;
  private Stack<Integer> deadNodes;

/*____________________________________________________________________________*/
/*                              CONSTRUCTORS                                  */
/*____________________________________________________________________________*/

  public Node(Registry rmiregistry) {
    registry = rmiregistry;
    isLeader = false;
  }


  public void becomeLeader() {
    System.out.println("Became leader");
    isLeader = true;
    LEADER_ID = NODE_ID;
    deadNodes = new Stack<Integer>();
  }
/*____________________________________________________________________________*/
/*                           INTERFACE IMPLEMENTATIONS                        */
/*                                NODE FUNCTIONS                              */
/*____________________________________________________________________________*/

  public String startElection(int callingNodeId) throws RemoteException {
    return "OK";
  }

  public String declareLeader(int leaderId) throws RemoteException {
    LEADER_ID = leaderId;
    if(LEADER_ID == NODE_ID) {
      becomeLeader();
    }
    return "OK";
  }

  public int getLeader() throws RemoteException {
    return LEADER_ID;
  }

  public String deliverMessage(String message) throws RemoteException {
    System.out.println("Received message: " + message);
    return "OK";
  }

/*____________________________________________________________________________*/
/*                           INTERFACE IMPLEMENTATIONS                        */
/*                           LEADER SPECIFIC FUNCTIONS                        */
/*____________________________________________________________________________*/

  public String publishMessage(String message) throws RemoteException {
    if(!isLeader) {
      return "Not Leader";
    }

    for(Integer id = 1; id < LEADER_ID ; id++) {
      try {
        NodeRemoteInterface stub = (NodeRemoteInterface) registry
        .lookup(id.toString());
        String response = stub.deliverMessage(message);
        if(response != "OK") {
          deadNodes.push(id);
        }
      } catch (Exception e) {
        System.out.println("An Exception occured on node " + id + " : "
        + e.getMessage());
        deadNodes.push(id);
      }
    }
    return "OK";
  }

  public int register() throws RemoteException {
    if(!isLeader) {
      return 0;
    }

    if(deadNodes.empty()) {
      return NODE_ID + 1;
    } else {
      return deadNodes.pop();
    }
  }
}
