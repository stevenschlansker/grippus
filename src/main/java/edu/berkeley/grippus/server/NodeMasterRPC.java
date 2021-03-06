package edu.berkeley.grippus.server;

import java.util.Set;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.fs.perm.Permission;
import edu.berkeley.grippus.storage.Block;

public interface NodeMasterRPC {
	/** joinCluster sends a join request which will add this NodeRPC to the 
	 *  canonical set list and propagate changes to the rest of the cluster.
	 *  Should only be sent to the master.
	 */
	Errno joinCluster(String myURL);

	/** Gets the canonical cluster member list. Should only be sent to the Master.
	 */
	Set<String> getOtherNodes();

	/** Removes self from the Master's cluster - should only be sent to the Master.
	 */
	boolean leaveCluster(String myURL);
	public String getClusterName();
	public String getClusterUUID();
	public DFile downloadMetadata();
	public Errno mkdir(DFileSpec dir, Permission perm);
	public Errno addEntry(DFileSpec parent, DFile child);
	public void updateMetadata(Block from, String path);
}
