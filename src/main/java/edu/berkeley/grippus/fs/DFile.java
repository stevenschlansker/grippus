package edu.berkeley.grippus.fs;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.perm.Permission;
import edu.berkeley.grippus.fs.perm.UndefinedPermissions;
import edu.berkeley.grippus.storage.Block;
import edu.berkeley.grippus.storage.Storage;

public abstract class DFile implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final Permission permissions;

	public DFile(String name, Permission perm) {
		this.name = name;
		this.permissions = perm;
	}

	public abstract Map<String, DFile> getChildren();

	public DFile find(DFileSpec path) {
		return find(path.getPath());
	}

	private DFile find(List<String> pathbits) {
		if (pathbits.isEmpty()) return this;
		final String first = pathbits.get(0);
		if (pathbits.isEmpty()) return this;
		if (first.isEmpty() || first.equals("."))
			return find(pathbits.subList(1, pathbits.size()));
		DFile child = getChildren().get(first);
		if (child != null)
			if (pathbits.size() == 1)
				return child;
			else
				return child.find(pathbits.subList(1, pathbits.size()));
		if (pathbits.size() > 1)
			throw new RuntimeException("No such file or directory");
		return new DirectoryPlaceholderDFile(this, first, new UndefinedPermissions());
	}

	public DFile find(String path) {
		List<String> pathbits = Arrays.asList(path.split("/"));
		return find(pathbits);
	}

	protected boolean nameValid(String name) {
		return name.matches("[a-z0-9A-Z_\\.-][a-zA-Z_0-9\\.-]*");
	}
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public Permission getPermissions() {
		return permissions;
	}

	/** On a directory entry, create it */
	public abstract Errno mkdir(Permission perm);

	/** On a directory, create the named child */
	public abstract Errno mkdir(String name, Permission perm);

	public abstract boolean isDirectory();

	public abstract Errno replaceEntry(DFile oldEntry, DMount newMount);

	public abstract Errno addEntry(DFile persistentDFile);

	public abstract DFile getParent();

	public boolean exists() {
		return true;
	}

	public abstract void replaceBlock(Block b);
	
	public InputStream open(Storage s, DFileSpec path) {
		throw new UnsupportedOperationException("Can't read this file "
				+ getClass() + " " + this);
	}
}
