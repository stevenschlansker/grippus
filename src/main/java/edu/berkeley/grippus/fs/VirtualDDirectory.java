package edu.berkeley.grippus.fs;

import java.util.Map.Entry;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import edu.berkeley.grippus.Errno;

public class VirtualDDirectory extends VirtualDFile {
	private static final long serialVersionUID = 1L;
	private final DFile parent;
	
	public VirtualDDirectory(String name, DFile parent, Permission perm) {
		super(name, perm);
		this.parent = parent;
		initializeChildren(parent);
	}

	/** for RootDFile's use, self-referential parent */
	protected VirtualDDirectory(String name, Permission perm) {
		super(name, perm);
		this.parent = this;
		initializeChildren(parent);
	}

	private void initializeChildren(DFile parent) {
		getChildren().put("..", parent);
		getChildren().put(".", this);
	}

	@Override
	public Errno mkdir(Permission perm) {
		return Errno.ERROR_EXISTS;
	}

	@Override
	public Errno mkdir(String name, Permission perm) {
		if (getChildren().containsKey(name))
			return Errno.ERROR_FILE_NOT_FOUND;
		if (!nameValid(name))
			return Errno.ERROR_BAD_NAME;
		getChildren().put(name, new VirtualDDirectory(name, this, perm));
		return Errno.SUCCESS;
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public Errno replaceEntry(DFile oldEntry, DMount newMount) {
		getChildren().put(nameForFile(oldEntry), newMount);
		return Errno.SUCCESS;
	}

	private String nameForFile(final DFile oldEntry) {
		return Collections2.filter(getChildren().entrySet(), new Predicate<Entry<String, DFile>>() {
			@Override public boolean apply(Entry<String, DFile> input) {
				return input.getValue().equals(oldEntry);
			}
		}).iterator().next().getKey();
	}
}
