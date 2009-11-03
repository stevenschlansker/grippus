package edu.berkeley.grippus.server;

import java.util.HashSet;
import org.apache.log4j.Logger;

import com.caucho.hessian.server.HessianServlet;

import edu.berkeley.grippus.Errno;

public class NodeRPCImpl extends HessianServlet implements NodeRPC {
	private static final long serialVersionUID = 1L;

	static Node myNode = Node.getNode();
	private static Logger logger = myNode.log.getLogger(NodeRPCImpl.class);

	public NodeRPCImpl() { /* nothing */ }

	@Override
	public boolean advertiseJoiningNode(NodeRPC joiner) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean advertiseLeavingNode(NodeRPC leaver) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public HashSet<String> getClusterList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMaster() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String joinCluster(String myURL) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean leaveCluster(String myURL) {
		// TODO Auto-generated method stub
		return false;
	}

	/*** 
	 * Connects to a master server and sets it to master server. 
	 * Sends the message to master server to broadcast its existence.
	 * @param masterServerURL
	 */
	public Errno connectToServer(String masterServerURL, String clusterPassword) {
		if (myNode == null) {
			myNode = Node.getNode();
		}
		return myNode.connectToServer(masterServerURL, clusterPassword);
	}
	
	/***
	 * Method to acquire a new node into the master server
	 */
	public void getNewNode(String newNodeURL) {
		if (myNode == null) {
			myNode = Node.getNode();
		}
		myNode.getNewNode(newNodeURL);
	}
	
	public String getMasterClusterName() {
		if (myNode == null) {
			myNode = Node.getNode();
		}
		return myNode.getClusterName();
	}
	
	public String getMasterClusterUUID() {
		if (myNode == null) {
			myNode = Node.getNode();
		}
		return myNode.getClusterID().toString();
	}
}
