package edu.berkeley.grippus.fs;

import java.io.Serializable;

import edu.berkeley.grippus.server.Node;

public class DFileSpec implements Serializable {
	private static final long serialVersionUID = 1L;
	private String path;

	@SuppressWarnings("unused")
	private DFileSpec() { /* for serialization */ }

	public DFileSpec(String path) {
		this.path = simplify(path);
	}

	private static String simplify(String path) {
		return path.replaceAll("//*", "/").replaceAll("/./", "/");
	}

	@Override
	public String toString() {
		return path;
	}

	public static final DFileSpec ROOT = new DFileSpec("/");

	public String getPath() {
		return path;
	}

	public DFile find(String path) {
		return Node.getNode().getVFS().find(this).find(path);
	}

	public DFileSpec append(String dirname) {
		if (dirname.charAt(0) == '/')
			return new DFileSpec(dirname);
		return new DFileSpec(path + "/" + dirname);
	}

	public String name() {
		return path.substring(path.lastIndexOf('/')+1);
	}

	public DFileSpec upOneLevel() {
		return new DFileSpec(path.substring(0, path.lastIndexOf('/')+1));
	}
}
