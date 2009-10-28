package edu.berkeley.grippus.fs;

import java.util.Set;

public class VFS {
	private DFile root = new DFile();

	public Set<DFile> ls(DFile cwd) {
		return cwd.getChildren();
	}

	public DFile resolve(String path) {
		if (path.equals("/")) return root;
		return null;
	}

}
