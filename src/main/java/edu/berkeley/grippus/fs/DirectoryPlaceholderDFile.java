package edu.berkeley.grippus.fs;

import edu.berkeley.grippus.Result;

public class DirectoryPlaceholderDFile extends DFile {
	
	private final DFile parent;
	private final String name;

	public DirectoryPlaceholderDFile(DFile parent, String name) {
		super(name);
		this.parent = parent;
		this.name = name;
	}

	@Override
	public Result mkdir() {
		return parent.mkdir(name);
	}

	@Override
	public Result mkdir(String name) {
		return Result.ERROR_FILE_NOT_FOUND;
	}
}
