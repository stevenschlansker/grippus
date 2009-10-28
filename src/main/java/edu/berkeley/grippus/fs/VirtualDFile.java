package edu.berkeley.grippus.fs;

import edu.berkeley.grippus.Errno;

public class VirtualDFile extends DFile {
	
	private final DFile parent;
	
	public VirtualDFile(String name, DFile parent) {
		super(name);
		this.parent = parent;
		getChildren().put("..", parent);
		getChildren().put(".", this);
	}

	@Override
	public Errno mkdir() {
		return Errno.ERROR_EXISTS;
	}

	@Override
	public Errno mkdir(String name) {
		if (getChildren().containsKey(name))
			return Errno.ERROR_FILE_NOT_FOUND;
		if (!nameValid(name))
			return Errno.ERROR_BAD_NAME;
		getChildren().put(name, new VirtualDFile(name, this));
		return Errno.SUCCESS;
	}
}
