package edu.berkeley.grippus.server;

import java.util.HashSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.caucho.hessian.server.HessianServlet;

import edu.berkeley.grippus.Errno;

public class NodeMasterRPCImpl extends HessianServlet implements NodeMasterRPC {
	private static final long serialVersionUID = 1L;

	private Node myNode;
	private Logger logger;

	public NodeMasterRPCImpl() { /* nothing */ }

	public NodeMasterRPCImpl(Node node) {
		myNode = node;
		logger = myNode.log.getLogger(NodeRPCImpl.class);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		myNode = (Node) config.getServletContext().getAttribute("node");
		logger = myNode.log.getLogger(NodeRPCImpl.class);
	}

	@Override
	public HashSet<String> getClusterList() {
		return (HashSet<String>) myNode.getClusterURLS();
	}

	@Override
	public Errno joinCluster(String myURL) {
		return myNode.masterAddSlave(myURL);
	}

	@Override
	public boolean leaveCluster(String leaverURL) {
		if(!myNode.isMaster()){
			logger.warn("Tried to leave non-master Node");
			return false;
		}
		return myNode.removeNodeAsMaster(leaverURL);
	}
	public String getMasterClusterName() {
		return myNode.getClusterName();
	}

	public String getMasterClusterUUID() {
		return myNode.getClusterID().toString();
	}
}
