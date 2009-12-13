package edu.berkeley.grippus.map;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.onionnetworks.fec.FECCodeFactory;
import com.onionnetworks.util.Buffer;

import edu.berkeley.grippus.fs.DFile;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.fs.PersistentDFile;
import edu.berkeley.grippus.fs.VFS;
import edu.berkeley.grippus.fs.perm.EveryonePermissions;
import edu.berkeley.grippus.storage.BlockList;
import edu.berkeley.grippus.storage.Storage;

public class FEC extends FileMapper {
	@Override
	public String execute(VFS vfs, Storage store, DFile in, DFileSpec outDir) {
		byte[] buf = new byte[CHUNK_SIZE];
		InputStream ins = in.open(store, null);
		BlockList bl = new BlockList();
		List<Buffer> input = new ArrayList<Buffer>();
		int off = 0;
		boolean done = false;
		do {
			off = 0;
			while (off < CHUNK_SIZE) {
				int nBytes;
				try {
					nBytes = ins.read(buf, off, CHUNK_SIZE - off);
				} catch (IOException e) {
					return "Failed: " + e.toString();
				}
				if (nBytes == -1) {
					done = true;
					break;
				}
				off += nBytes;
			}
			while (off < CHUNK_SIZE) {
				buf[off] = 0;
				off++;
			}
			input.add(new Buffer(buf, 0, CHUNK_SIZE));
		} while (!done);
		if (input.size() % 2 == 1)
			input.add(new Buffer(new byte[CHUNK_SIZE], 0, CHUNK_SIZE));
		Buffer[] output = new Buffer[input.size() * 3 / 2];
		int[] index = new int[output.length];
		for (int i = 0; i < index.length; i++) {
			output[i] = new Buffer(new byte[CHUNK_SIZE], 0, CHUNK_SIZE);
			index[i] = i;
		}
		FECCodeFactory.getDefault().createFECCode(input.size(), output.length)
		.encode(input.toArray(new Buffer[input.size()]), output, index);
		for (int i = 0; i < output.length; i++) {
			try {
				bl.append(store.blockFor(ByteBuffer.wrap(output[i].b)));
			} catch (IOException e) {
				return "Local failure: " + e;
			}
		}
		vfs.addEntry(outDir, new PersistentDFile(bl, in.getName() + ".fec-"
				+ CHUNK_SIZE,
				new EveryonePermissions()));
		return "Success";
	}
	@Override
	protected int getChunkSize() {
		return 8192;
	}

	@Override
	protected ByteBuffer transform(ByteBuffer in) {
		throw new AssertionError("not reached");
	}

}
