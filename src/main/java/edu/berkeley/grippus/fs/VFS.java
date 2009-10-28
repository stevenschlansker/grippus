package edu.berkeley.grippus.fs;

import java.util.Map;

public class VFS {
	private DFile root = new VirtualDFile("%ROOT%");

	public Map<String, DFile> ls(DFile cwd) {
		return cwd.getChildren();
	}

	public DFile resolve(DFileSpec path) {
		if (path.getPath().equals("/")) return root;
		return root.find(path);
	}

	public DFile find(DFileSpec path) {
		return root.find(path);
	}
}
