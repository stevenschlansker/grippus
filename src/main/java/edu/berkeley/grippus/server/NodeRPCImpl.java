package edu.berkeley.grippus.server;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.server.HessianServlet;

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
	public void connectToServer(String masterServerURL) {
		try {
			if (myNode == null) {
				myNode = Node.getNode();
			}
			HessianProxyFactory factory = new HessianProxyFactory();	
			factory.setUser("grippus");
			factory.setPassword("password");
			NodeRPC master = (NodeRPC) factory.create(NodeRPC.class, masterServerURL);
			myNode.setMasterServer(master);
			myNode.setMasterURL(masterServerURL);
			myNode.setClusterName(master.getMasterClusterName());
			byte[] blah = master.getMasterClusterUUID().getBytes();
			UUID clusterID = UUID.nameUUIDFromBytes(blah);
			myNode.setClusterID(clusterID);
			String nodeURL = "http://"+myNode.getIpAddress()+":"+String.valueOf(myNode.getPort())+"/node";
			myNode.getMasterServer().getNewNode(nodeURL);
		} catch (MalformedURLException e) {
			logger.error("Could not find host " + masterServerURL, e);
		}
		if (myNode == null) {
			myNode = Node.getNode();
		}
		myNode.connectToServer(masterServerURL);
	}
	
	/***
	 * Method to acquire a new node into the master server
	 */
	public void getNewNode(String newNodeURL) {
		try {
			if (myNode == null) {
				myNode = Node.getNode();
			}
			HessianProxyFactory factory = new HessianProxyFactory();
			factory.setUser("grippus");
			factory.setPassword("password");
			NodeRPC newNode = (NodeRPC) factory.create(NodeRPC.class,newNodeURL);
			if (myNode.isMaster()) {
				for (NodeRPC node : myNode.getClusterMembers()) {
					node.getNewNode(newNodeURL);
					newNode.getNewNode(myNode.getClusterURLs().get(node));
				}
			}
			myNode.getClusterMembers().add(newNode);
			myNode.getClusterURLs().put(newNode, newNodeURL);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Bad URL", e);
		}
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
