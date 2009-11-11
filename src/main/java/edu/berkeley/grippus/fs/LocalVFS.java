package edu.berkeley.grippus.fs;

import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.server.DPassthroughMount;

public class LocalVFS extends VFS {
	private DFile root = new RootDFile();

	@Override
	public Map<String, DFile> ls(DFile cwd) {
		return cwd.getChildren();
	}

	@Override
	public DFile resolve(DFileSpec path) {
		if (path.getPath().equals("/")) return root;
		return root.find(path);
	}

	private DFile find(DFileSpec path) {
		return root.find(path);
	}

	@Override
	public Errno mount(DFileSpec where, String realPath, Permission perm) {
		DFile oldEntry = find(where);
		DFile oldParent = find(where.upOneLevel());
		if (!oldEntry.isDirectory() || oldEntry.getChildren().size() > 2)
			return Errno.ERROR_ILLEGAL_ARGUMENT;
		DMount newMount = new DPassthroughMount(where, realPath, oldParent, perm);
		return oldParent.replaceEntry(oldEntry, newMount);
	}

	@Override
	public Errno mkdir(DFileSpec dir, Permission perm) {
		return find(dir).mkdir(perm);
	}
}
