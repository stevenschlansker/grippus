package edu.berkeley.grippus.server;

import edu.berkeley.grippus.fs.DFileSpec;

public interface NodeManagementRPC {

	public String version(String cmd);
	public String terminate(String cmd);
	public String status(String cmd);
	public String initCluster(String cmd, String clusterName);
	public String disconnect(String cmd);
	public String ls(String cmd, DFileSpec path);

}
