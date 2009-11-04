package edu.berkeley.grippus.fs;

import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.server.DPassthroughMount;

public class VFS {
	private DFile root = new RootDFile();

	public Map<String, DFile> ls(DFile cwd) {
		return cwd.getChildren();
	}

	public DFile resolve(DFileSpec path) {
		if (path.getPath().equals("/")) return root;
		return root.find(path);
	}

	public DFile find(DFileSpec path) {
		return root.find(path);
	}

	public DFileSpec canonicalize(DFileSpec path) {
		return path;
	}

	public Errno mount(DFileSpec where, String realPath, Permission perm) {
		DFile oldEntry = find(where);
		DFile oldParent = find(where.upOneLevel());
		if (!oldEntry.isDirectory() || oldEntry.getChildren().size() > 2)
			return Errno.ERROR_ILLEGAL_ARGUMENT;
		DMount newMount = new DPassthroughMount(where, realPath, oldParent, perm);
		return oldParent.replaceEntry(oldEntry, newMount);
	}
}
