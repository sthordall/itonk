package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
*  Interface to be implemented by nodes wishing to subscribe to news
*/
public interface SubscriberRemoteInterface extends Remote {

  /**
  *  Function called by publisher, to publish news to subscriber
  */
  void publishNews(string news) throws RemoteException;

}
