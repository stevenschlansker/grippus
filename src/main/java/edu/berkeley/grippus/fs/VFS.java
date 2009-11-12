package edu.berkeley.grippus.fs;

import java.util.Map;

import edu.berkeley.grippus.Errno;

public abstract class VFS {
	public abstract Errno mount(DFileSpec where, String realPath, Permission perm);
	public abstract Errno mkdir(DFileSpec dir, Permission perm);
	protected abstract DFile getRoot();
	public DFileSpec canonicalize(DFileSpec path) {
		return path;
	}
	public Map<String, DFile> ls(DFile cwd) {
		return cwd.getChildren();
	}
	public DFile resolve(DFileSpec path) {
		if (path.getPath().equals("/")) return getRoot();
		return getRoot().find(path);
	}
	public DFile getMetadata() {
		return getRoot();
	}
}