package edu.berkeley.grippus.server;

import java.util.HashSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.caucho.hessian.server.HessianServlet;

import edu.berkeley.grippus.Errno;

public class NodeRPCImpl extends HessianServlet implements NodeRPC {
	private static final long serialVersionUID = 1L;

	private Node myNode;
	private Logger logger;

	public NodeRPCImpl() { /* nothing */ }

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		myNode = (Node) config.getServletContext().getAttribute("node");
		logger = myNode.log.getLogger(NodeRPCImpl.class);
	}

	@Override
	public boolean advertiseJoiningNode(String joinerURL) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean advertiseLeavingNode(String leaverURL) {
		myNode.removeNodeLocal(leaverURL);
		return true;
	}

	@Override
	public HashSet<String> getClusterList() {
		return (HashSet<String>) myNode.getClusterURLS();
	}

	@Override
	public String getMaster() {
		return myNode.getMasterURL();
	}

	@Override
	public String joinCluster(String myURL) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean leaveCluster(String leaverURL) {
		if(!myNode.isMaster()){
			logger.warn("Tried to leave non-master Node");
			return false;
		}
		return myNode.removeNodeAsMaster(leaverURL);
	}

	/*** 
	 * Connects to a master server and sets it to master server. 
	 * Sends the message to master server to broadcast its existence.
	 * @param masterServerURL
	 */
	public Errno connectToServer(String masterServerURL, String clusterPassword) {
		return myNode.connectToServer(masterServerURL, clusterPassword);
	}
	
	/***
	 * Method to acquire a new node into the master server
	 */
	public void getNewNode(String newNodeURL) {
		myNode.getNewNode(newNodeURL);
	}
	
	public String getMasterClusterName() {
		return myNode.getClusterName();
	}
	
	public String getMasterClusterUUID() {
		return myNode.getClusterID().toString();
	}
}
