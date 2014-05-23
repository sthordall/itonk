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

/*____________________________________________________________________________*/
/*                            NODE INITIALIZATION                             */
/*____________________________________________________________________________*/

  public void findLeader() {
    Integer incrementalId = 0;

    //Lookup and find incremently
    while(LEADER_ID == 0) {
      incrementalId++;
      try {
        NodeRemoteInterface stub = (NodeRemoteInterface) registry
        .lookup(incrementalId.toString());

        LEADER_ID = stub.getLeader();

        //If tried id's from 1-10, declare leadership.

      } catch (Exception e) {
        System.out.println("No node on id " + e.getMessage());
      }

      if(incrementalId == 10) {
        System.out.println("No leader found, declaring node as leader");
        NODE_ID = 1;
        try {
          declareLeader(NODE_ID);
        } catch (Exception e) {
          System.out.println("An Exception occured: " + e.getMessage());
        }

      }
    }
  }

  public void registerNode() {
    try {
      NodeRemoteInterface stub = (NodeRemoteInterface) UnicastRemoteObject
      .exportObject(this, 0);

      if(isLeader) {
        registry.bind(NODE_ID.toString(), stub);
      } else {
        NodeRemoteInterface leader = (NodeRemoteInterface) registry
        .lookup(LEADER_ID.toString());

        NODE_ID = leader.register();
        registry.bind(NODE_ID.toString(), stub);
        if(NODE_ID > LEADER_ID) {
          becomeLeader();
        }
      }

      System.out.println("Node up and running - ID: " + NODE_ID);

    } catch (Exception e) {
      System.out.println("An Exception occured: " + e.getMessage());
    }
  }

/*____________________________________________________________________________*/
/*                               HELPER FUNCTIONS                             */
/*____________________________________________________________________________*/

  public void becomeLeader() {
    System.out.println("Became leader");
    isLeader = true;
    LEADER_ID = NODE_ID;
    deadNodes = new Stack<Integer>();

    for(Integer id = 1; id < LEADER_ID ; id++) {
      try {
        NodeRemoteInterface stub = (NodeRemoteInterface) registry
        .lookup(id.toString());
        String response = stub.declareLeader(LEADER_ID);
        if(response != "OK") {
          deadNodes.push(id);
        }
      } catch (Exception e) {
        System.out.println("An Exception occured on node " + id + " : "
        + e.getMessage());
        deadNodes.push(id);
      }
    }
  }

  public void sendMessage(String message) {
    try {
      if(isLeader) {
        System.out.println("Is leader, publishing message");
        publishMessage(message);
      } else {
        System.out.println("Publishing message with leader");
        NodeRemoteInterface stub = (NodeRemoteInterface) registry
        .lookup(LEADER_ID.toString());
        stub.publishMessage(message);
      }
    } catch (Exception e) {
        System.out.println("An Exception occured: " + e.getMessage());
    }
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
    isLeader = false;
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
