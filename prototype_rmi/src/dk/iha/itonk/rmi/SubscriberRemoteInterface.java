package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
*  Interface to be implemented by nodes wishing to subscribe to news
*/
public interface SubscriberRemoteInterface extends Remote {

  /**
  *  Function called by publisher, to publish movie news to subscriber
  */
  void publishMovieNews(string movieNews) throws RemoteException;

  /**
  *  Function called by publisher, to publish tech news to subscriber
  */
  void publishTechNews(string techNews) throws RemoteException;
}
