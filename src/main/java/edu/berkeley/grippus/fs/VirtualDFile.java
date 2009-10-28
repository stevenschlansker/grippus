package edu.berkeley.grippus.fs;

import edu.berkeley.grippus.Result;

public class VirtualDFile extends DFile {
	
	private final DFile parent;
	
	public VirtualDFile(String name, DFile parent) {
		super(name);
		this.parent = parent;
		getChildren().put("..", parent);
		getChildren().put(".", this);
	}

	@Override
	public Result mkdir() {
		return Result.ERROR_EXISTS;
	}

	@Override
	public Result mkdir(String name) {
		if (getChildren().containsKey(name))
			return Result.ERROR_FILE_NOT_FOUND;
		if (!nameValid(name))
			return Result.ERROR_BAD_NAME;
		getChildren().put(name, new VirtualDFile(name, this));
		return Result.SUCCESS;
	}
}
