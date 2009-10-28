package edu.berkeley.grippus.server;

import java.util.UUID;

public interface NodeRPC {

	public String getMasterClusterName();
	
	public UUID getMasterClusterUUID();
	
	public void getNewNode(String newNodeURL);
}
