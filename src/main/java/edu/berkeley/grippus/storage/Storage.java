package edu.berkeley.grippus.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


public interface Storage {
	BlockList chunkify(File src) throws IOException;

	InputStream readBlock(Block from) throws IOException;

	void createFile(byte[] digest, byte[] fileData) throws IOException;

	Block blockFor(ByteBuffer buf) throws IOException;
}
