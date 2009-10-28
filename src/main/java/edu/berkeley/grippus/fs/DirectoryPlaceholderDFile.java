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
}
