package edu.berkeley.grippus.fs;

import edu.berkeley.grippus.fs.perm.Permission;


public abstract class DMount extends DFile {
	private static final long serialVersionUID = 1L;

	public DMount(String name, Permission permissions) {
		super(name, permissions);
	}
}
