package edu.berkeley.grippus.fs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DFile {

	public Map<String, DFile> getChildren() {
		return new HashMap<String, DFile>();
	}

	public DFileSpec find(DFileSpec path) {
		List<String> pathbits = Arrays.asList(path.getPath().split("/"));
		return find(pathbits);
	}

	private DFileSpec find(List<String> pathbits) {
		// TODO Auto-generated method stub
		return null;
	}
}
