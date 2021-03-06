package edu.berkeley.grippus.server;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.storage.Block;

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

	byte[] getFile(Block block, int blockLength);

	String getNodeRef();

	String mapFile(DFile file, String className, DFileSpec destDir);
}
