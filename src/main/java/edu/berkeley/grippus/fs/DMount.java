package edu.berkeley.grippus.fs;


public abstract class DMount extends DFile {
	private static final long serialVersionUID = 1L;

	public DMount(String name, Permission permissions) {
		super(name, permissions);
	}
}
