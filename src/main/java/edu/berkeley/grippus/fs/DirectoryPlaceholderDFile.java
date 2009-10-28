package edu.berkeley.grippus.fs;

public class DirectoryPlaceholderDFile extends DFile {
	
	private final DFile parent;
	private final String name;

	public DirectoryPlaceholderDFile(DFile parent, String name) {
		this.parent = parent;
		this.name = name;
	}
}
