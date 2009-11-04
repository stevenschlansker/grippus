package edu.berkeley.grippus.fs;


public abstract class DMount extends DFile {
	public DMount(String name, Permission permissions) {
		super(name, permissions);
	}
}
