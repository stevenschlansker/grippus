package edu.berkeley.grippus.server;

import java.util.HashSet;
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
	 * @return
	 */
	public void connectToServer(String masterServerURL) {
		try {
			if (myNode == null) {
				myNode = Node.getNode();
			}
			HessianProxyFactory factory = new HessianProxyFactory();	
			factory.setUser("grippus");
			ConsoleReader console = new ConsoleReader();
			String pw = console.readLine("Cluster password: ", '*');
			factory.setPassword(pw);
			NodeRPC master = (NodeRPC) factory.create(NodeRPC.class, masterServerURL);
			myNode.setMasterServer(master);
			myNode.setMasterURL(masterServerURL);
			myNode.setClusterName(master.getMasterClusterName());
			byte[] blah = master.getMasterClusterUUID().getBytes();
			UUID clusterID = UUID.nameUUIDFromBytes(blah);
			myNode.setClusterID(clusterID);
			String nodeURL = "http:\\"+myNode.getIpAddress()+":"+String.valueOf(myNode.getPort());
//			myNode.getMasterServer().getNewNode(nodeURL);
		} catch (MalformedURLException e) {
			/* empty URL */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
