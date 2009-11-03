package edu.berkeley.grippus.server;

import com.caucho.hessian.server.HessianServlet;
import java.util.UUID;

import edu.berkeley.grippus.Result;
import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.fs.VFS;

public class NodeManagementRPCImpl extends HessianServlet implements NodeManagementRPC {
	private static final long serialVersionUID = 1L;
	static Node managedNode = Node.getNode(); // TODO: ugly fucking hack!!! :( :( :(

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
	public Result initCluster(String cmd, String clusterName) {
		managedNode.initCluster(clusterName);
		return Result.SUCCESS_TOPOLOGY_CHANGE;
	}

	@Override
	public String disconnect(String cmd) {
		managedNode.disconnect();
		return status(cmd);
	}

	@Override
	public String ls(String cmd, DFileSpec path) {
		VFS vfs = managedNode.getVFS();
		StringBuilder result = new StringBuilder();
		result.append(path+":\n");
		for (DFile f : vfs.ls(vfs.resolve(path)).values()) {
			result.append(f);
		}
		return result.toString();
	}

	@Override
	public Result mkdir(String cmd, DFileSpec dir) {
		return Result.ERROR_EXISTS;
	}
	
	public void connectToNetwork(String cmd, String masterURL) {
		managedNode.connectToMaster(masterURL);
	}
}
