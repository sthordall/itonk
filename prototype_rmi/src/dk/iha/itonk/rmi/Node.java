package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
*  Implementation of NodeRemoteInterface
*/
public class Node implements NodeRemoteInterface {

  public int NODE_ID;
  public int LEADER_ID;

  public String RegistryHost;
  public int RegistryPort;

  private Registry registry;
  private Node leader;
  private List<Node> nodes;

/*____________________________________________________________________________*/
/*                              CONSTRUCTORS                                  */
/*____________________________________________________________________________*/

  public Node(String registryHost, int registryPort) {
    RegistryHost = registryHost;
    RegistryPort = registryPort;
  }


/*____________________________________________________________________________*/
/*                            NODE INITIALIZATION                             */
/*____________________________________________________________________________*/

  public void start() {
    setupRegistry();
    findLeader();
    register();
  }

  private void setupRegistry() {
    registry = LocateRegistry.getRegistry(RegistryHost, RegistryPort);
  }

  private void findLeader() {
    int incrementalId = 0;

    //Lookup and find incremently
    while(LEADER_ID == null) {
      incrementalId++;
      try {
        Node stub = (Node) registry.lookup(incrementalId);
        LEADER_ID = stub.getLeader();
      } catch (Exception e) {
        System.out.println("An Exception occured: " + e.getMessage());
      }
    }
  }

  private void register() {
    try {
      Node leader = (Node) registry.lookup(LEADER_ID);
      NODE_ID = leader.Register();
    } catch (Exception e) {
      System.out.println("An Exception occured: " + e.getMessage());
    }
  }


/*____________________________________________________________________________*/
/*                           INTERFACE IMPLEMENTATIONS                        */
/*                                NODE FUNCTIONS                              */
/*____________________________________________________________________________*/

  public String startElection(int callingNodeId) throws RemoteException {

  }

  public String declareLeader(int leaderId) throws RemoteException {
    LEADER_ID = leaderId;
    return "OK";
  }

  public int getLeader() throws RemoteException {
    return LEADER_ID;
  }

  public String deliverMessage(String message) throws RemoteException {
    System.out.println("Received message: " + message)
    return "OK";
  }

/*____________________________________________________________________________*/
/*                           INTERFACE IMPLEMENTATIONS                        */
/*                           LEADER SPECIFIC FUNCTIONS                        */
/*____________________________________________________________________________*/

  public String publishMessage(String message) throws RemoteException {

  }

  public int Register() throws RemoteException {

  }


}
