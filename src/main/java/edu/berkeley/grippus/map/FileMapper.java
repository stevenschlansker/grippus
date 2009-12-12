package edu.berkeley.grippus.map;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.fs.PersistentDFile;
import edu.berkeley.grippus.fs.VFS;
import edu.berkeley.grippus.fs.perm.EveryonePermissions;
import edu.berkeley.grippus.storage.BlockList;
import edu.berkeley.grippus.storage.Storage;

public abstract class FileMapper {
	protected final int CHUNK_SIZE = getChunkSize();
	public final String execute(VFS vfs, Storage store, DFile in,
			DFileSpec outDir) {
		ByteBuffer buf = ByteBuffer.allocate(CHUNK_SIZE);
		InputStream ins = in.open(store, null); //TODO: This is so dangerous....
		BlockList bl = new BlockList();
		int off = 0;
		do {
			off = 0;
			while (off < CHUNK_SIZE) {
				int nBytes;
				try {
					nBytes = ins.read(buf.array(), off, CHUNK_SIZE - off);
				} catch (IOException e) {
					return "Failed: " + e.toString();
				}
				if (nBytes == -1)
					break;
				off += nBytes;
			}
			buf.limit(off);
			ByteBuffer result = transform(buf);
			try {
				bl.append(store.blockFor(result));
			} catch (IOException e) {
				return "Local failure: " + e;
			}
		} while (off == CHUNK_SIZE);
		vfs.addEntry(outDir, new PersistentDFile(bl, in.getName() + ".mapped",
				new EveryonePermissions()));
		return "Success";
	}

	protected abstract ByteBuffer transform(ByteBuffer in);

	protected abstract int getChunkSize();
}
