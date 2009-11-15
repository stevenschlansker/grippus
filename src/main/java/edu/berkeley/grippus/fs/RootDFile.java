package edu.berkeley.grippus.fs;

public class RootDFile extends VirtualDDirectory {
	private static final long serialVersionUID = 1L;

	public RootDFile() {
		super("%ROOT%", new EveryonePermissions());
	}
}
