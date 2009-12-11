package edu.berkeley.grippus.fs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.berkeley.grippus.fs.perm.Permission;

public abstract class VirtualDFile extends DFile {
	private static final long serialVersionUID = 1L;
	private final Map<String, DFile> children = new ConcurrentHashMap<String, DFile>();
	public VirtualDFile(String name, Permission perm) {
		super(name, perm);
	}
	@Override
	public Map<String, DFile> getChildren() {
		return children;
	}
}
