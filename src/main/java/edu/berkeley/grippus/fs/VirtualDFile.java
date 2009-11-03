package edu.berkeley.grippus.fs;

import java.util.HashMap;
import java.util.Map;

public abstract class VirtualDFile extends DFile {
	private Map<String, DFile> children = new HashMap<String, DFile>();
	public VirtualDFile(String name) {
		super(name);
	}
	@Override
	public Map<String, DFile> getChildren() {
		return children;
	}
}
