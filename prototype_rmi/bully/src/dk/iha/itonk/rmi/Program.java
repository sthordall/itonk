package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

    System.out.println("Enter message to send:");
        try {
          BufferedReader br = new BufferedReader(
          new InputStreamReader(System.in));
          while(true) {
            String message = br.readLine();
            System.out.print("You entered:");
            System.out.println(message);
            node.sendMessage(message);
          }
        }
        catch (IOException e){
            System.out.println("Error reading from user");
        }
  }

  public static Registry setupRegistry(String registryHost, int registryPort) {
    try {
      return LocateRegistry.getRegistry(registryHost, registryPort);
    } catch(Exception e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

}
