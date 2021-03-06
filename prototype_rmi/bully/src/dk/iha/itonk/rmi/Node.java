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
  private Boolean isElecting;
  private Registry registry;
  private Node leader;
  private Stack<Integer> deadNodes;

/*____________________________________________________________________________*/
/*                              CONSTRUCTORS                                  */
/*____________________________________________________________________________*/

  public Node(Registry rmiregistry) {
    registry = rmiregistry;
    isLeader = false;
    isElecting = false;
  }

/*____________________________________________________________________________*/
/*                               ELECTION METHODS                             */
/*____________________________________________________________________________*/

  public String bullyElection() {
    if(isLeader) {
      System.out.println("Do not start election, already leader");
      becomeLeader();
    } else {
      isElecting = true;
      Integer count = 0;

      for(Integer id = (NODE_ID + 1); id <= LEADER_ID ; id++) {
        try {
            NodeRemoteInterface stub = (NodeRemoteInterface) registry
            .lookup(id.toString());
            String response = stub.startElection(NODE_ID);

            if(response.equals("OK")) {
              System.out.println("Election sent to: " + id.toString());
              count++;
            }
          } catch (Exception e) {
            System.out.println("Could not deliver election to node " + id);
        }
      }

      if(count == 0) {
        System.out.println("Election done, this node is new leader.");
        try{
          registry.unbind(LEADER_ID.toString());
        } catch (Exception e) {
          System.out.println("Exception occured when unbinding"
          + "previous leader");
        }
        becomeLeader();
      }
    }
    return "OK";
  }

  public String ringElection(Integer fromId) {
    if(isLeader) {
      System.out.println("Do not start election, already leader");
      becomeLeader();
    } else {
      isElecting = true;
      Integer id = NODE_ID + 1;
      Boolean isVotePassed = false;
      System.out.println("Election received from node " + fromId );
      while(isVotePassed == false && isElecting == true) {
        try {
          NodeRemoteInterface stub = (NodeRemoteInterface) registry
          .lookup(id.toString());

          String response = stub.startElection(NODE_ID);

          if(response.equals("OK")) {
            System.out.println("Election sent to: " + id.toString());
            isVotePassed = true;
          }
        } catch (Exception e) {
          System.out.println("Could not deliver election to node " + id);
          System.out.println("Trying next node in ring");
          id++;
          if(id > LEADER_ID) {
            isVotePassed = true;
            becomeLeader();
          }
        }
      }
    }
    return "OK";
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
    isElecting = false;
    isLeader = true;
    LEADER_ID = NODE_ID;
    deadNodes = new Stack<Integer>();

    for(Integer id = 1; id < LEADER_ID ; id++) {
      try {
        NodeRemoteInterface stub = (NodeRemoteInterface) registry
        .lookup(id.toString());
        String response = stub.declareLeader(LEADER_ID);
      } catch (Exception e) {
        System.out.println("An Exception occured on node " + id + " : "
        + e.getMessage());
        registerDeadNode(id);
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
        System.out.println("Leader not responding, starting election");
        try {
          startElection(NODE_ID);
        } catch (Exception ex) {
          System.out.println("An error occurred when starting election: "
          + ex.getMessage());
        }
    }
  }

  public void registerDeadNode(Integer id) {
    try {
      registry.unbind(id.toString());
      deadNodes.push(id);
    } catch (Exception e) {
      System.out.println("Exception when registering dead node: "
      + e.getMessage());
    }
  }
/*____________________________________________________________________________*/
/*                           INTERFACE IMPLEMENTATIONS                        */
/*                                NODE FUNCTIONS                              */
/*____________________________________________________________________________*/

  public String startElection(int callingNodeId) throws RemoteException {
    if(callingNodeId != NODE_ID) {
      System.out.println("Election received from: " + callingNodeId);
    }

    return bullyElection();
    //return ringElection(callingNodeId);
  }

  public String declareLeader(int leaderId) throws RemoteException {
    LEADER_ID = leaderId;
    isLeader = false;
    isElecting = false;
    if(LEADER_ID == NODE_ID) {
      becomeLeader();
    } else {
      System.out.println("New leader, node " + LEADER_ID);
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

    System.out.println("Received message to publish: " + message);

    for(Integer id = 1; id < LEADER_ID ; id++) {
      try {
        if(deadNodes.search(id) == -1) {
          NodeRemoteInterface stub = (NodeRemoteInterface) registry
          .lookup(id.toString());
          String response = stub.deliverMessage(message);
          System.out.println("Message delivered to: " + id.toString());
        } else {
            System.out.println("Node " + id + " is dead, not delivering");
        }
      } catch (Exception e) {
        System.out.println("Could not deliver message to node " + id +
        ", added to deadnodes");
        registerDeadNode(id);
      }
    }
    return "OK";
  }

  public int register() throws RemoteException {
    if(!isLeader) {
      return 0;
    }

    if(deadNodes.empty()) {
      System.out.println("New node registering, id = " + (NODE_ID + 1));
      return (NODE_ID + 1);
    } else {
      System.out.println("New node registering, id = " + deadNodes.peek());
      return (int) deadNodes.pop();
    }
  }
}
