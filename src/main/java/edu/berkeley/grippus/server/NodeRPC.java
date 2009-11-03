package edu.berkeley.grippus.server;

public interface NodeRPC {

	public String getMasterClusterName();
	
	public String getMasterClusterUUID();
	
	public void getNewNode(String newNodeURL);
}
