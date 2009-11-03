package edu.berkeley.grippus.fs;

import edu.berkeley.grippus.Errno;

public class DirectoryPlaceholderDFile extends DFile {
	
	private final DFile parent;
	private final String name;

	public DirectoryPlaceholderDFile(DFile parent, String name) {
		super(name);
		this.parent = parent;
		this.name = name;
	}

	@Override
	public Errno mkdir() {
		return parent.mkdir(name);
	}

	@Override
	public Errno mkdir(String name) {
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
}
