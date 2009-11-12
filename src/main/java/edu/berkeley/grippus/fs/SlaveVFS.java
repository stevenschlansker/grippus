package edu.berkeley.grippus.fs;

import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.server.NodeMasterRPC;

public class SlaveVFS extends VFS {
	private final NodeMasterRPC master;
	public SlaveVFS(NodeMasterRPC master) {
		this.master = master;
	}
	@Override
	public Map<String, DFile> ls(DFile cwd) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Errno mkdir(DFileSpec dir, Permission perm) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Errno mount(DFileSpec where, String realPath, Permission perm) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public DFile resolve(DFileSpec path) {
		// TODO Auto-generated method stub
		return null;
	}
}
