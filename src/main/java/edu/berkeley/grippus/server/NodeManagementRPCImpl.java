package edu.berkeley.grippus.server;

import com.caucho.hessian.server.HessianServlet;

public class NodeManagementRPCImpl extends HessianServlet implements NodeManagementRPC {
	private static final long serialVersionUID = 1L;
	static Node managedNode; // TODO: ugly fucking hack!!! :( :( :(

	public NodeManagementRPCImpl() { /* do nothing */ }

	@Override
	public String version() {
		return "0.1";
	}
}
