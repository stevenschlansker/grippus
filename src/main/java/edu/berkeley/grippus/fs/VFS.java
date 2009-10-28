package edu.berkeley.grippus.fs;

import java.util.Set;

public class VFS {
	private DFile root = new DFile();

	public Set<DFile> ls(DFile cwd) {
		return cwd.getChildren();
	}

	public DFile resolve(DFileSpec path) {
		if (path.getPath().equals("/")) return root;
		return null;
	}

}
