package edu.berkeley.grippus.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.perm.EveryonePermissions;
import edu.berkeley.grippus.fs.perm.Permission;
import edu.berkeley.grippus.storage.Block;
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
	public synchronized DFile getMetadata() {
		return getRoot();
	}

	public Errno copyRecursive(String src, DFileSpec dest, Storage storage) {
		File srcRoot = new File(src);
		if (!srcRoot.isDirectory())
			return copyFile(srcRoot, dest, storage);
		Errno result = mkdir(dest, new EveryonePermissions());
		sync();
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
			return addEntry(dest.upOneLevel(),
					new PersistentDFile(storage.chunkify(src), src.getName(),
							new EveryonePermissions()));
		} catch (FileNotFoundException e) {
			return Errno.ERROR_FILE_NOT_FOUND;
		} catch (IOException e) {
			return Errno.ERROR_IO;
		}
	}

	public Errno addEntry(DFileSpec parent, DFile child) {
		return resolve(parent).addEntry(child);
	}

	public void sync() { /* master VFS is always in sync */
	}
	public void updateMetadata(Block from, String path) {
		DFile d = this.resolve(new DFileSpec(path));
		if (!d.isDirectory()) {
			//How the fuck do I replace this block with a new block?!
			d.replaceBlock(from);
		}


	}
}
