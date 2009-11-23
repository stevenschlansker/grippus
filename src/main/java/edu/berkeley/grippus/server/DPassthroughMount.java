package edu.berkeley.grippus.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.fs.DMount;
import edu.berkeley.grippus.fs.perm.Permission;

public class DPassthroughMount extends DMount {
	private static final long serialVersionUID = 1L;
	private final PassthroughFile root;
	private final File mountedDirectory;

	public DPassthroughMount(DFileSpec dfs, String basePath, DFile parent, Permission perm) {
		super(dfs.name(), perm);
		mountedDirectory = new File(basePath);
		root = new PassthroughFile(dfs.name(), mountedDirectory, parent, perm);
	}

	@Override
	public Errno mkdir(Permission perm) {
		return Errno.ERROR_READ_ONLY;
	}

	@Override
	public Errno mkdir(String name, Permission perm) {
		return Errno.ERROR_READ_ONLY;
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public Errno replaceEntry(DFile oldEntry, DMount newMount) {
		return Errno.ERROR_NOT_SUPPORTED;
	}

	@Override
	public Map<String, DFile> getChildren() {
		return root.getChildren();
	}

	@Override
	public Errno addEntry(DFile persistentDFile) {
		return Errno.ERROR_READ_ONLY;
	}

	@Override
	public DFile getParent() {
		return root.getParent();
	}

	private class PassthroughFile extends DFile {
		private static final long serialVersionUID = 1L;
		private final File me;
		private final DFile parent;
		public PassthroughFile(String name, File whoIAm, DFile parent, Permission perm) {
			super(name,perm);
			me = whoIAm;
			this.parent = parent;
		}
		@Override public boolean isDirectory() {
			return me.isDirectory();
		}
		@Override public Errno mkdir(Permission perm) {
			return Errno.ERROR_READ_ONLY;
		}
		@Override public Errno mkdir(String name, Permission perm) {
			return Errno.ERROR_READ_ONLY;
		}
		@Override public Errno replaceEntry(DFile oldEntry, DMount newMount) {
			return Errno.ERROR_READ_ONLY;
		}

		@Override
		public Errno addEntry(DFile persistentDFile) {
			return Errno.ERROR_NOT_SUPPORTED;
		}

		@Override
		public Map<String, DFile> getChildren() {
			Map<String, DFile> result = new HashMap<String, DFile>();
			for (String child : me.list()) {
				result.put(child, new PassthroughFile(child, new File(me, child), this, getPermissions()));
			}
			result.put(".", this);
			result.put("..", parent);
			return result;
		}

		@Override
		public DFile getParent() {
			return parent;
		}
	}
}
