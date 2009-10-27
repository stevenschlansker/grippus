package edu.berkeley.grippus.server;

import com.caucho.hessian.server.HessianServlet;

public class NodeRPCImpl extends HessianServlet {
	private static final long serialVersionUID = 1L;
	private Node myNode;

	public NodeRPCImpl() { /* nothing */ }
	
	NodeRPCImpl(Node node) {
		myNode = node;
	}

}
