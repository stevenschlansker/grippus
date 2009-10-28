package edu.berkeley.grippus.server;

public interface NodeManagementRPC {

	public String version(String cmd);
	public String terminate(String cmd);
	public String status(String cmd);
	public String initCluster(String cmd, String clusterName);
	public String disconnect(String cmd);
	public String ls(String cmd, String path);

}
