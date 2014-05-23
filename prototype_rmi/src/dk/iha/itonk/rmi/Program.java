package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Program {

/*____________________________________________________________________________*/
/*                              MAIN METHOD                                   */
/*____________________________________________________________________________*/
  public static void main(String args[])
  {
    Registry registry = setupRegistry(args[0], Integer.parseInt(args[1]));
    Node node = new Node(registry);
    node.findLeader();
    node.registerNode();
  }

  public static Registry setupRegistry(string registryHost, int registryPort) {
    try {
      return LocateRegistry.getRegistry(registryHost, registryPort);
    } catch(Exception e) {
      System.out.println(e.getMessage());
    }
  }

}
