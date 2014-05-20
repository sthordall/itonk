package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
*  Interface to be implemented by nodes wishing to publish news
*/
public interface PublisherRemoteInterface extends Remote {

  /**
  *  Used by subscriber nodes wishing to subscribe to movie news
  */
  void subscribeMovieNews(string subscriberId) throws RemoteException;

  /**
  * Used by subscriber nodes wishing to subscribe to tech news
  */
  void subscribeTechNews(string subscriberId) throws RemoteException;
}
