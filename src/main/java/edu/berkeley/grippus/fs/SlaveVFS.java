package edu.berkeley.grippus.fs;


import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianRuntimeException;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.perm.EveryonePermissions;
import edu.berkeley.grippus.fs.perm.Permission;
import edu.berkeley.grippus.server.Node;
import edu.berkeley.grippus.server.NodeMasterRPC;
import edu.berkeley.grippus.util.Periodic;

public class SlaveVFS extends VFS {
	private final NodeMasterRPC master;
	private static final Logger LOG = Logger.getLogger(SlaveVFS.class);
	private volatile DFile root = new VirtualDDirectory("%TEMPROOT%",
			new EveryonePermissions());
	private final Node myNode;
	@SuppressWarnings("unused")
	private final Periodic updater = new Periodic(500, "VFS update thread") {
		@Override protected void fire() {
			if (myNode.isRunning())
				sync();
			else
				die();
		}
	};
	public SlaveVFS(Node myNode, NodeMasterRPC master) {
		this.myNode = myNode;
		this.master = master;
	}
	@Override
	public synchronized Errno mkdir(DFileSpec dir, Permission perm) {
		return master.mkdir(dir, perm);
	}
	@Override
	public synchronized Errno mount(DFileSpec where, String realPath,
			Permission perm) {
		return Errno.ERROR_NOT_SUPPORTED; // TODO implement
	}
	@Override
	protected synchronized DFile getRoot() {
		return root;
	}

	@Override
	public synchronized Errno addEntry(DFile parent, DFile child) {
		return master.addEntry(parent, child);
	}

	@Override
	public synchronized void sync() {
		super.sync();
		try {
			root = master.downloadMetadata();
		} catch (HessianRuntimeException e) {
			LOG.error("Could not download metadata!", e);
		}
	}
}
