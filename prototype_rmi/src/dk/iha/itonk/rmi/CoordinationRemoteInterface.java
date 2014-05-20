package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
*  Interface to be implemented by all nodes in system
*/
public interface CoordinationRemoteInterface extends Remote {

  /**
  *  Function called by node when initiating an election
  */
  string election(string nodeId) throws RemoteException;

  /**
  *  Function called by newly elected leader, to notify about new role as leader
  */
  string coordinate(string leaderId) throws RemoteException;
}
