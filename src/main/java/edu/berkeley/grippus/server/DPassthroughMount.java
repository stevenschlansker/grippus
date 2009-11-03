package edu.berkeley.grippus.server;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.fs.DMount;

public class DPassthroughMount extends DMount {

	public DPassthroughMount(DFileSpec dfs) {
		super(dfs.name());
		// TODO Auto-generated constructor stub
	}

	@Override
	public Errno mkdir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Errno mkdir(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public Errno replaceEntry(DFile oldEntry, DMount newMount) {
		return Errno.ERROR_NOT_SUPPORTED;
	}
}
