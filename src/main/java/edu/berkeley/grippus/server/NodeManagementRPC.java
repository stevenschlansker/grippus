package edu.berkeley.grippus.server;

import edu.berkeley.grippus.Result;
import edu.berkeley.grippus.fs.DFileSpec;

public interface NodeManagementRPC {

	public String version(String cmd);
	public String terminate(String cmd);
	public String status(String cmd);
	public Result initCluster(String cmd, String clusterName);
	public String disconnect(String cmd);
	public String ls(String cmd, DFileSpec path);
	public Result mkdir(String cmd, DFileSpec dir);
	public void connectToNetwork(String cmd, String masterURL);
}
