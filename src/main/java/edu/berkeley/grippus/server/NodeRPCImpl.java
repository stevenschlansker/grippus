package edu.berkeley.grippus.server;

import java.io.IOException;
import java.net.MalformedURLException;

import java.util.UUID;

import jline.ConsoleReader;

import com.caucho.hessian.server.HessianServlet;
import com.caucho.hessian.client.HessianProxyFactory;
import com.sun.jdmk.comm.MalformedHttpException;

public class NodeRPCImpl extends HessianServlet implements NodeRPC {
	private static final long serialVersionUID = 1L;

	static Node myNode = Node.getNode();
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
		if (myNode == null) {
			myNode = Node.getNode();
		}
		myNode.connectToServer(masterServerURL);
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
