package edu.berkeley.grippus.server;

import java.net.MalformedURLException;
import java.util.UUID;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.server.HessianServlet;

public class NodeRPCImpl extends HessianServlet implements NodeRPC {
	private static final long serialVersionUID = 1L;

	static Node myNode = Node.getNode();
	public NodeRPCImpl() { /* nothing */ }

	/*** 
	 * Connects to a master server and sets it to master server. 
	 * Sends the message to master server to broadcast its existence.
	 * @param masterServerURL
	 * @return
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
			
		}
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
			
		}
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
