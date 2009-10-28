package edu.berkeley.grippus.fs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DFile {

	public Map<String, DFile> getChildren() {
		return new HashMap<String, DFile>();
	}

	public DFile find(DFileSpec path) {
		return find(path.getPath());
	}

	private DFile find(List<String> pathbits) {
		if (pathbits.isEmpty()) return this;
		DFile child = getChildren().get(pathbits.get(0));
		if (child != null) 
			if (pathbits.size() == 1)
				return child;
			else
				return child.find(pathbits.subList(1, pathbits.size()));
		if (pathbits.size() > 1)
			throw new RuntimeException("No such file or directory");
		return new DirectoryPlaceholderDFile(this, pathbits.get(0));
	}

	public DFile find(String path) {
		List<String> pathbits = Arrays.asList(path.split("/"));
		return find(pathbits);
	}
}
