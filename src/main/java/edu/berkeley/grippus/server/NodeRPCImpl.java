package edu.berkeley.grippus.server;

import java.net.MalformedURLException;

import java.util.UUID;
import com.caucho.hessian.server.HessianServlet;
import com.caucho.hessian.client.HessianProxyFactory;
import com.sun.jdmk.comm.MalformedHttpException;

public class NodeRPCImpl extends HessianServlet implements NodeRPC {
	private static final long serialVersionUID = 1L;

	private Node myNode;
	public NodeRPCImpl() { /* nothing */ }
	
	NodeRPCImpl(Node node) {
	}

	/*** 
	 * Connects to a master server and sets it to master server. 
	 * Sends the message to master server to broadcast its existence.
	 * @param masterServerURL
	 * @return
	 */
	public void connectToServer(String masterServerURL) {
		try {
			HessianProxyFactory factory = new HessianProxyFactory();			
			NodeRPC master = (NodeRPC) factory.create(NodeRPC.class, masterServerURL);
			myNode.setMasterServer(master);
			myNode.setMasterURL(masterServerURL);
			myNode.setClusterName(master.getMasterClusterName());
			myNode.setClusterID(master.getMasterClusterUUID());
			myNode.getMasterServer().getNewNode("http:\\<external ip>"+myNode.getPort());
		} catch (MalformedURLException e) {
			
		}
	}
	
	/***
	 * Method to acquire a new node into the master server
	 */
	public void getNewNode(String newNodeURL) {
		try {
			HessianProxyFactory factory = new HessianProxyFactory();
			NodeRPC newNode = (NodeRPC) factory.create(NodeRPC.class,newNodeURL);
			myNode.getClusterMembers().add(newNode);
			myNode.getClusterURLs().put(newNode, newNodeURL);
			if (myNode.isMaster()) {
				for (NodeRPC node : myNode.getClusterMembers()) {
					node.getNewNode(newNodeURL);
					newNode.getNewNode(myNode.getClusterURLs().get(node));
				}
			}
		} catch (MalformedURLException e) {
			
		}
	}
	
	public String getMasterClusterName() {
		return myNode.getClusterName();
	}
	
	public UUID getMasterClusterUUID() {
		return myNode.getClusterID();
	}
	
	public void getClusterUUID(String masterServer) {
		
	}
}
