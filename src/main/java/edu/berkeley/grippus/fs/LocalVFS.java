package edu.berkeley.grippus.fs;


import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.perm.Permission;
import edu.berkeley.grippus.server.DPassthroughMount;

public class LocalVFS extends VFS {
	private DFile root = new RootDFile();

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
	@Override
	protected DFile getRoot() {
		return root;
	}
}
