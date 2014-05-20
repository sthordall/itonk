package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
*  Interface to be implemented by leader
*/
public interface LeaderRemoteInterface extends Remote {
  /**
  *  Function called on leader, to get list of pusblishers.
  */
  string[] getPublishers(string subject) throws RemoteException;

  /**
  *  Function called on leader, to get list of subscribers.
  */
  string[] getSubscribers(string subject) throws RemoteException;

  /*
  *  Function called on leader, to register as new subscriber.
  */
  void registerSubscriber(string subscriberId, string topic);

  /*
  *  Function called on leader, to register as new publisher.
  */
  void registerPublisher(string publisherId, string topic);

}
