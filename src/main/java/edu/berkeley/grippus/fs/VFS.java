package edu.berkeley.grippus.fs;

import java.io.File;
import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.perm.EveryonePermissions;
import edu.berkeley.grippus.fs.perm.Permission;

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

	public Errno copyRecursive(String src, DFileSpec dest) {
		File srcRoot = new File(src);
		if (!srcRoot.isDirectory())
			return copyFile(src, dest);
		Errno result = getRoot().mkdir(dest.getPath(),
				new EveryonePermissions());
		if (result != Errno.SUCCESS)
			return result;
		for (File child : srcRoot.listFiles()) {
			result = copyRecursive(child.getAbsolutePath(), new DFileSpec(dest
					.getPath()
					+ "/" + child.getName()));
			if (result != Errno.SUCCESS)
				return result;
		}
		return Errno.SUCCESS;
	}

	private Errno copyFile(String src, DFileSpec dest) {
		throw new AssertionError("Not implemented");
	}
}