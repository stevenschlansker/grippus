package edu.berkeley.grippus.server;

import java.util.HashSet;

import com.caucho.hessian.server.HessianServlet;

public class NodeRPCImpl extends HessianServlet implements NodeRPC {
	private static final long serialVersionUID = 1L;

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
	public String joinCluster() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean leaveCluster() {
		// TODO Auto-generated method stub
		return false;
	}

}
