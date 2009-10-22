package edu.berkeley.grippus.server;

import java.io.File;

public class BackingStore {

	public BackingStore(File root) {
		if (!root.exists()) root.mkdir();
		if (!root.isDirectory())
			throw new RuntimeException("Store root " + root + " is not a directory :(");
		
	}

}
