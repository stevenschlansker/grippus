package edu.berkeley.grippus.fs;

import edu.berkeley.grippus.Errno;

public class RootDFile extends VirtualDDirectory {
	public RootDFile() {
		super("%ROOT%");
	}
}
