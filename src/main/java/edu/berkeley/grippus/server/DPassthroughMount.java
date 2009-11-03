package edu.berkeley.grippus.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.fs.DMount;

public class DPassthroughMount extends DMount {

	private final PassthroughFile root;
	private final File mountedDirectory;
	
	public DPassthroughMount(DFileSpec dfs, String basePath, DFile parent) {
		super(dfs.name());
		mountedDirectory = new File(basePath);
		root = new PassthroughFile(dfs.name(), mountedDirectory, parent);
	}

	@Override
	public Errno mkdir() {
		return Errno.ERROR_READ_ONLY;
	}

	@Override
	public Errno mkdir(String name) {
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
	
	private class PassthroughFile extends DFile {
		private final File me;
		private final DFile parent;
		public PassthroughFile(String name, File whoIAm, DFile parent) {
			super(name);
			me = whoIAm;
			this.parent = parent;
		}
		@Override public boolean isDirectory() {
			return me.isDirectory();
		}
		@Override public Errno mkdir() {
			return Errno.ERROR_READ_ONLY;
		}
		@Override public Errno mkdir(String name) {
			return Errno.ERROR_READ_ONLY;
		}
		@Override public Errno replaceEntry(DFile oldEntry, DMount newMount) {
			return Errno.ERROR_READ_ONLY;
		}

		@Override
		public Map<String, DFile> getChildren() {
			Map<String, DFile> result = new HashMap<String, DFile>();
			for (String child : me.list()) {
				result.put(child, new PassthroughFile(child, new File(me, child), this));
			}
			result.put(".", this);
			result.put("..", parent);
			return result;
		}
	}
}
