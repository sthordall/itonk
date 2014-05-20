package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
*  Interface to be implemented by all nodes in system
*/
public interface NodeRemoteInterface extends Remote {

  /**
  *  Function called by node when initiating an election, recipiant node returns
  *  'OK'
  */
  public String election(String callingNodeId) throws RemoteException;

  /**
  *  Function called by newly elected leader, to notify about new role as leader
  *  Node returns 'OK'
  */
  public String coordinate(String leaderId) throws RemoteException;

  /**
  *  Called by newly registered node to get leader
  */
  public String getLeader(String callingNodeId);

  /**
  *  Called by nodes on leader, takes message and publishes to rest of
  *  nodes in system. Leader returns 'OK'.
  */
  public String publishMessage(String messageToPublish);

  /**
  *  Called by leader on nodes, to deliver message. Nodes respond with 'OK'
  */
  public String deliverMessage(String messageToSupply);

}