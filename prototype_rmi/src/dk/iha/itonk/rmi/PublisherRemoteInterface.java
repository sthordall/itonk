package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
*  Interface to be implemented by nodes wishing to publish news
*/
public interface PublisherRemoteInterface extends Remote {

  /**
  *  Used by leader to assign subscriber nodes wishing to subscribe
  */
  void subscribeNews(string subscriberId) throws RemoteException;

}
