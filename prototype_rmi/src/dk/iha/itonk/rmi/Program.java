package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Program {

  public static String registryHost;
  public static Integer registryPort;
  public static Node node;
  public static Registry registry;

/*____________________________________________________________________________*/
/*                              MAIN METHOD                                   */
/*____________________________________________________________________________*/
  public static void main(String args[])
  {
    registryHost = args[0];
    registryPort = Integer.parseInt(args[1]);
    setupRegistry();
    node = new Node(registry);
    findLeader();
    registerNode();
  }
  /*____________________________________________________________________________*/
/*                            NODE INITIALIZATION                             */  /*____________________________________________________________________________*/

  public static void setupRegistry() {
    try {
      registry = LocateRegistry.getRegistry(registryHost, registryPort);
    } catch(Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public static void findLeader() {
    Integer incrementalId = 0;

    //Lookup and find incremently
    while(node.LEADER_ID == 0) {
      incrementalId++;
      try {
        NodeRemoteInterface stub = (NodeRemoteInterface) registry
        .lookup(incrementalId.toString());

        node.LEADER_ID = stub.getLeader();

        //If tried id's from 1-10, declare leadership.

      } catch (Exception e) {
        System.out.println("An Exception occured: " + e.getMessage());
      }

      if(incrementalId == 10) {
        System.out.println("No leader found, declaring node as leader");
        node.NODE_ID = 1;
        try {
          node.declareLeader(node.NODE_ID);
        } catch (Exception e) {
          System.out.println("An Exception occured: " + e.getMessage());
        }

      }
    }
  }

  public static void registerNode() {
    try {
      NodeRemoteInterface stub = (NodeRemoteInterface) UnicastRemoteObject
      .exportObject(node, 0);

      if(node.isLeader) {
        registry.bind(node.NODE_ID.toString(), stub);
      } else {
        NodeRemoteInterface leader = (NodeRemoteInterface) registry
        .lookup(node.LEADER_ID.toString());

        node.NODE_ID = leader.register();
        registry.bind(node.NODE_ID.toString(), stub);
      }

      System.out.println("Node up and running - ID: " + node.NODE_ID);

    } catch (Exception e) {
      System.out.println("An Exception occured: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
