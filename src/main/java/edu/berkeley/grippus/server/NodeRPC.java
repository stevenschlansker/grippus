package edu.berkeley.grippus.server;
import java.util.HashSet;

public interface NodeRPC {

	/** Returns the master NodeRPC of this NodeRPC's cluster
	 */
	String getMaster();
	
	/** joinCluster sends a join request which will add this NodeRPC to the 
	 *  canonical set list and propagate changes to the rest of the cluster.
	 *  Should only be sent to the master.
	 */
	String joinCluster(String myURL);
	
	/** Gets the canonical cluster member list. Should only be sent to the Master.
	 */
	HashSet<String> getClusterList();
	
	/** Removes self from the Master's cluster - should only be sent to the Master.
	 */
	boolean leaveCluster(String myURL);

	/** Sends this NodeRPC an update that the joiner node is joining the cluster. Idempotent.
	 *  Should only be called by the MasterNode to a slave. 
	 */
	boolean advertiseJoiningNode(String joinerURL);
	
	/** Sends this NodeRPC an update that the leaver node is leaving the cluster. Idempotent.
	 *  Should only be called by the MasterNode to a slave. 
	 */
	boolean advertiseLeavingNode(String leaverURL);
	
	
	
	public String getMasterClusterName();
	
	public String getMasterClusterUUID();
	
	public void getNewNode(String newNodeURL);
}
