package edu.berkeley.grippus.server;

import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.caucho.hessian.server.HessianServlet;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.fs.Permission;

public class NodeMasterRPCImpl extends HessianServlet implements NodeMasterRPC {
	private static final long serialVersionUID = 1L;

	private Node myNode;
	private Logger logger;

	public NodeMasterRPCImpl() { /* nothing */ }

	public NodeMasterRPCImpl(Node node) {
		myNode = node;
		logger = Logger.getLogger(NodeRPCImpl.class);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		myNode = (Node) config.getServletContext().getAttribute("node");
		logger = Logger.getLogger(NodeRPCImpl.class);
	}

	@Override
	public Set<String> getOtherNodes() {
		return myNode.getClusterURLS();
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

	@Override
	public String getClusterName() {
		return myNode.getClusterName();
	}

	@Override
	public String getClusterUUID() {
		return myNode.getClusterID().toString();
	}
	@Override
	public DFile downloadMetadata() {
		return myNode.getVFS().getMetadata();
	}
	@Override
	public Errno mkdir(DFileSpec dir, Permission perm) {
		return myNode.getVFS().mkdir(dir, perm);
	}
}
