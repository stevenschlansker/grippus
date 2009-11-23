package edu.berkeley.grippus.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;

import edu.berkeley.grippus.server.Node;
import edu.berkeley.grippus.util.FileSize;

public class LocalFilesystemStorage implements Storage {
	private final int CHUNK_SIZE = 10*1024*1024;
	private final int MMAP_SIZE = 1024*1024*1024;
	private final long maxStoreSize;
	private static final Logger LOG = Logger
	.getLogger(LocalFilesystemStorage.class);
	private final File root;

	public LocalFilesystemStorage(Node node, File root) {
		if (!root.exists())
			root.mkdir();
		if (!root.isDirectory())
			throw new RuntimeException("Store root " + root
					+ " is not a directory :(");
		maxStoreSize = FileSize.parseSize(node.getConf().getString(
		"store.maxsize"));
		LOG.debug("Using up to " + maxStoreSize + " bytes for storage...");
		this.root = root;
	}
	@Override public BlockList chunkify(File src) throws IOException {
		FileInputStream fis = new FileInputStream(src);
		long length = src.length();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("Could not SHA-512 :(", e);
		}
		BlockList result = new BlockList();
		for (long mapoff = 0; mapoff <= length; mapoff += MMAP_SIZE) {
			ByteBuffer buf = fis.getChannel().map(MapMode.READ_ONLY, 0, length);
			int chunklen = (int) Math.min(length - mapoff, CHUNK_SIZE);
			for (int off = 0; off <= chunklen; off += CHUNK_SIZE) {
				int limit = (int) Math.min(off + CHUNK_SIZE, length - mapoff - off);
				buf.position(off);
				buf.limit(limit);
				md.update(buf);
				byte[] digest = md.digest();
				buf.position(off);
				saveChunk(digest, buf);
				result.append(new Block(digest, limit));
			}
		}
		return result;
	}

	private void saveChunk(byte[] digest, ByteBuffer buf) throws IOException {
		File dir = dirForDigest(digest);
		if (!dir.exists())
			if (!dir.mkdirs())
				throw new IOException("Could not make parents of " + dir);
		File storage = new File(dir, nameForDigest(digest));
		if (storage.exists()) {
			byte[] oldbuf = new byte[buf.remaining()];
			buf.get(oldbuf);
			if (Arrays.equals(StreamUtils
					.getBytes(new FileInputStream(storage)), oldbuf))
				LOG.info("Found duplicated block!  " + storage.getName());
			else
				LOG.error("OH FUCK HASH COLLISION " + storage.getAbsolutePath());
		} else {
			FileOutputStream fos = new FileOutputStream(storage);
			int len = buf.remaining();
			fos.getChannel().write(buf);
			fos.close();
			LOG.info("Wrote chunk of size " + len + " to "
					+ storage.getAbsolutePath());
		}
	}
	private String nameForDigest(byte[] digest) {
		return Hex.encodeHexString(digest);
	}

	private File dirForDigest(byte[] digest) {
		return new File(new File(new File(root, String.format("%02X%02X",
				digest[0], digest[1])), String.format("%02X%02X", digest[2],
						digest[3])), String.format("%02X%02X", digest[4], digest[5]));
	}

	@Override
	public InputStream readBlock(Block from) throws IOException {
		try {
			return new FileInputStream(new File(dirForDigest(from.digest),
					nameForDigest(from.digest)));
		} catch (FileNotFoundException e) {
			LOG.error("Could not find block " + from + "!!!!");
			throw new IOException("Corrupted file", e);
		}
	}
}
