package edu.berkeley.grippus.fs;

import edu.berkeley.grippus.Result;

public class VirtualDFile extends DFile {
	public VirtualDFile(String name) {
		super(name);
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
		getChildren().put(name, new VirtualDFile(name));
		return Result.SUCCESS;
	}
}
