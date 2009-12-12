package edu.berkeley.grippus.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.perm.EveryonePermissions;
import edu.berkeley.grippus.fs.perm.Permission;
import edu.berkeley.grippus.storage.Block;
import edu.berkeley.grippus.storage.Storage;

public abstract class VFS {
	public abstract Errno mount(DFileSpec where, String realPath, Permission perm);
	public abstract Errno mkdir(DFileSpec dir, Permission perm);
	protected abstract DFile getRoot();

	protected abstract Logger getLogger();
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

	private final ExecutorService copyExecutor = Executors
	.newCachedThreadPool();

	public Errno copyRecursive(String src, final DFileSpec dest, final Storage storage) {
		File srcRoot = new File(src);
		if (!srcRoot.isDirectory())
			return copyFile(srcRoot, dest, storage);
		Errno result = mkdir(dest, new EveryonePermissions());
		sync();
		if (result != Errno.SUCCESS && result != Errno.ERROR_EXISTS)
			return result;
		List<Future<Errno>> results = new ArrayList<Future<Errno>>();
		for (final File child : srcRoot.listFiles()) {
			results.add(copyExecutor.submit(new Callable<Errno>() {
				@Override
				public Errno call() throws Exception {
					return copyRecursive(child.getAbsolutePath(),
							new DFileSpec(dest.getPath() + "/"
									+ child.getName()), storage);
				}
			}));
		}
		for (Future<Errno> r : results) {
			try {
				if (r.get() != Errno.SUCCESS)
					return r.get();
			} catch (InterruptedException e) {
				getLogger().error("Interrupted", e);
				return Errno.INTERNAL_ERROR;
			} catch (ExecutionException e) {
				getLogger().error("Execution exception", e);
				return Errno.INTERNAL_ERROR;
			}
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

	protected void sync() { /* master VFS is always in sync */
	}
	public void updateMetadata(Block from, String path) {
		DFile d = this.resolve(new DFileSpec(path));
		if (!d.isDirectory()) {
			//How the fuck do I replace this block with a new block?!
			d.replaceBlock(from);
		}


	}
	public void terminate() {
		copyExecutor.shutdown();
	}
}
