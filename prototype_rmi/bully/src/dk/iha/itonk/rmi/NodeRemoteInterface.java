package dk.iha.itonk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
*  Interface to be implemented by all nodes in system
*/
public interface NodeRemoteInterface extends Remote {

/*____________________________________________________________________________*/
/*                                NODE FUNCTIONS                              */
/*____________________________________________________________________________*/
  /**
  *  Function called by node when initiating an election, recipiant node returns
  *  'OK'
  */
  public String startElection(int callingNodeId) throws RemoteException;

  /**
  *  Function called by newly elected leader, to notify about new role as leader
  *  Node returns 'OK'
  */
  public String declareLeader(int leaderId) throws RemoteException;

  /**
  *  Called by newly registered node to get leaderId
  */
  public int getLeader() throws RemoteException;

  /**
  *  Called by leader on nodes, to deliver message. Nodes respond with 'OK'
  */
  public String deliverMessage(String message) throws RemoteException;

/*____________________________________________________________________________*/
/*                           LEADER SPECIFIC FUNCTIONS                        */
/*____________________________________________________________________________*/

  /**
  *  Called by nodes on leader, takes message and publishes to rest of
  *  nodes in system. Leader returns 'OK'.
  */
  public String publishMessage(String message) throws RemoteException;

  /**
  *  Called by new node on leader, to register. Leader returns assigned ID.
  */
  public int register() throws RemoteException;

}
