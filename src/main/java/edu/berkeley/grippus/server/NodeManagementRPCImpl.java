package edu.berkeley.grippus.server;

import com.caucho.hessian.server.HessianServlet;

public class NodeManagementRPCImpl extends HessianServlet implements NodeManagementRPC {
	private static final long serialVersionUID = 1L;
	static Node managedNode; // TODO: ugly fucking hack!!! :( :( :(

	public NodeManagementRPCImpl() { /* do nothing */ }

	@Override
	public String version(String cmd) {
		return "0.1";
	}

	@Override
	public String terminate(String cmd) {
		managedNode.terminate();
		return "Success";
	}

	@Override
	public String status(String cmd) {
		return managedNode.status();
	}

	@Override
	public String initCluster(String cmd, String clusterName) {
		String result = managedNode.initCluster(clusterName) ? "Success" : "Failed";
		return result + "\n" + status(cmd);
	}

	@Override
	public String disconnect(String cmd) {
		managedNode.disconnect();
		return status(cmd);
	}
}
