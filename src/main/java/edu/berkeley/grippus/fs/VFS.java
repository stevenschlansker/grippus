package edu.berkeley.grippus.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.perm.EveryonePermissions;
import edu.berkeley.grippus.fs.perm.Permission;
import edu.berkeley.grippus.storage.Storage;

public abstract class VFS {
	public abstract Errno mount(DFileSpec where, String realPath, Permission perm);
	public abstract Errno mkdir(DFileSpec dir, Permission perm);
	protected abstract DFile getRoot();
	public DFileSpec canonicalize(DFileSpec path) {
		return path;
	}
	public Map<String, DFile> ls(DFile cwd) {
		return cwd.getChildren();
	}
	public DFile resolve(DFileSpec path) {
		if (path.getPath().equals("/")) return getRoot();
		return getRoot().find(path);
	}
	public DFile getMetadata() {
		return getRoot();
	}

	public Errno copyRecursive(String src, DFileSpec dest, Storage storage) {
		File srcRoot = new File(src);
		if (!srcRoot.isDirectory())
			return copyFile(srcRoot, dest, storage);
		Errno result = resolve(dest).mkdir(new EveryonePermissions());
		if (result != Errno.SUCCESS && result != Errno.ERROR_EXISTS)
			return result;
		for (File child : srcRoot.listFiles()) {
			result = copyRecursive(child.getAbsolutePath(), new DFileSpec(dest
					.getPath() + "/" + child.getName()), storage);
			if (result != Errno.SUCCESS)
				return result;
		}
		return Errno.SUCCESS;
	}

	private Errno copyFile(File src, DFileSpec dest, Storage storage) {
		if (!src.isFile())
			return Errno.SUCCESS;
		try {
			DFile df = resolve(dest);
			if (df.exists())
				return Errno.SUCCESS;
			return df.getParent().addEntry(
					new PersistentDFile(storage.chunkify(src), src.getName(),
							new EveryonePermissions()));
		} catch (FileNotFoundException e) {
			return Errno.ERROR_FILE_NOT_FOUND;
		} catch (IOException e) {
			return Errno.ERROR_IO;
		}
	}
}