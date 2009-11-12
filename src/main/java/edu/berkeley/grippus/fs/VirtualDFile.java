package edu.berkeley.grippus.fs;

import java.util.HashMap;
import java.util.Map;

public abstract class VirtualDFile extends DFile {
	private Map<String, DFile> children = new HashMap<String, DFile>();
	public VirtualDFile(String name, Permission perm) {
		super(name, perm);
	}
	@Override
	public Map<String, DFile> getChildren() {
		return children;
	}
}
