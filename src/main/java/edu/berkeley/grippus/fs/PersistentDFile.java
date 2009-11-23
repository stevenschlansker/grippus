package edu.berkeley.grippus.fs;

import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.perm.Permission;
import edu.berkeley.grippus.storage.BlockList;

public class PersistentDFile extends DFile {
	private static final long serialVersionUID = 1L;
	private static BlockList data;
	public PersistentDFile(BlockList data, String name, Permission perm) {
		super(name, perm);
		this.data = data;
	}

	@Override
	public Map<String, DFile> getChildren() {
		return null;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}
	@Override
	public Errno mkdir(Permission perm) {
		return Errno.ERROR_NOT_SUPPORTED;
	}
	@Override
	public Errno mkdir(String name, Permission perm) {
		return Errno.ERROR_NOT_SUPPORTED;
	}
	@Override
	public Errno replaceEntry(DFile oldEntry, DMount newMount) {
		return Errno.ERROR_NOT_SUPPORTED;
	}
	@Override
	public Errno addEntry(PersistentDFile persistentDFile) {
		return Errno.ERROR_NOT_SUPPORTED;
	}

	@Override
	public DFile getParent() {
		throw new UnsupportedOperationException("Can't get the parent of a file");
	}
}
