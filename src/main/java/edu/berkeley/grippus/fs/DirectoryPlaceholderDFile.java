package edu.berkeley.grippus.fs;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.perm.Permission;

public class DirectoryPlaceholderDFile extends VirtualDFile {
	private static final long serialVersionUID = 1L;
	private final DFile parent;
	private final String name;

	public DirectoryPlaceholderDFile(DFile parent, String name, Permission perm) {
		super(name, perm);
		this.parent = parent;
		this.name = name;
	}

	@Override
	public Errno mkdir(Permission perm) {
		return parent.mkdir(name, perm);
	}

	@Override
	public Errno mkdir(String name, Permission perm) {
		return Errno.ERROR_FILE_NOT_FOUND;
	}

	@Override
	public boolean isDirectory() {
		return false; // doesn't really exist yet
	}

	@Override
	public Errno replaceEntry(DFile oldEntry, DMount newMount) {
		return Errno.ERROR_NOT_SUPPORTED;
	}

	@Override
	public Errno addEntry(DFile persistentDFile) {
		return Errno.ERROR_NOT_SUPPORTED;
	}

	@Override
	public DFile getParent() {
		return parent;
	}

	@Override
	public boolean exists() {
		return false;
	}
}
