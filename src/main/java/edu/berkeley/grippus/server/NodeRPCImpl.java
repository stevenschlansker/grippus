package edu.berkeley.grippus.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.storage.Block;

public class NodeRPCImpl extends HessianServlet implements NodeRPC {
	private static final long serialVersionUID = 1L;
	private Node myNode;

	public NodeRPCImpl() { /* nothing */ }

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		myNode = (Node) config.getServletContext().getAttribute("node");
	}

	@Override
	public Errno advertiseJoiningNode(String joinerURL) {
		return myNode.addPeer(joinerURL);
	}

	@Override
	public boolean advertiseLeavingNode(String leaverURL) {
		myNode.removeNodeLocal(leaverURL);
		return true;
	}

	public synchronized byte[] getFile(Block block, int blockLength) {
		return myNode.getFile(block, blockLength);
	}

	@Override
	public String getMasterURL() {
		return myNode.getMasterURL();
	}

	/***
	 * Connects to a master server and sets it to master server.
	 * Sends the message to master server to broadcast its existence.
	 * @param masterServerURL
	 */
	public Errno connectToServer(String masterServerURL, String clusterPassword) {
		return myNode.connectToServer(masterServerURL, clusterPassword);
	}

	@Override
	public String getNodeRef() {
		return myNode.getNodeRef();
	}

	@Override
	public String mapFile(DFile file, String className, DFileSpec dest) {
		return myNode.mapFile(file, className, dest);
	}
}
