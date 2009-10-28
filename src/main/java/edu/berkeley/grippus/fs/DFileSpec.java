package edu.berkeley.grippus.fs;

import java.io.Serializable;

public class DFileSpec implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String path;

	public DFileSpec(String path) {
		this.path = path;
	}
	
	@Override
	public String toString() {
		return path;
	}

	public static final DFileSpec ROOT = new DFileSpec("/");

	public String getPath() {
		return path;
	}

	public DFileSpec find(String path) {
		return ROOT;
	}

}
