package edu.berkeley.grippus.fs;

import java.util.Map;

import edu.berkeley.grippus.Errno;

public interface VFS {

	public abstract Map<String, DFile> ls(DFile cwd);
	public abstract DFile resolve(DFileSpec path);
	public abstract DFileSpec canonicalize(DFileSpec path);
	public abstract Errno mount(DFileSpec where, String realPath, Permission perm);
	public abstract Errno mkdir(DFileSpec dir, Permission perm);

}