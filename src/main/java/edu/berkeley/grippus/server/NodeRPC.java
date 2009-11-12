package edu.berkeley.grippus.server;

import edu.berkeley.grippus.Errno;

public interface NodeRPC {
	/** Returns the master NodeRPC of this NodeRPC's cluster
	 */
	String getMasterURL();
	/** Sends this NodeRPC an update that the joiner node is joining the cluster. Idempotent.
	 *  Should only be called by the MasterNode to a slave. 
	 */
	Errno advertiseJoiningNode(String joinerURL);
	/** Sends this NodeRPC an update that the leaver node is leaving the cluster. Idempotent.
	 *  Should only be called by the MasterNode to a slave. 
	 */
	boolean advertiseLeavingNode(String leaverURL);
	String getNodeRef();
}
