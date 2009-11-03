package edu.berkeley.grippus.server;

import java.io.File;
import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.fs.DMount;

public class DPassthroughMount extends DMount {

	private PassthroughFile root;
	private final File mountedDirectory;
	
	public DPassthroughMount(DFileSpec dfs, String basePath) {
		super(dfs.name());
		mountedDirectory = new File(basePath);
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
		public PassthroughFile(String name, File whoIAm) {
			super(name);
			me = whoIAm;
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
			// TODO Auto-generated method stub
			return null;
		}

	}
}
