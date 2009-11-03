package edu.berkeley.grippus.fs;

import edu.berkeley.grippus.Errno;

public class RootDFile extends VirtualDFile {
	public RootDFile() {
		super("%ROOT%");
	}
}
