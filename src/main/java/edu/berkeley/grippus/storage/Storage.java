package edu.berkeley.grippus.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public interface Storage {
	BlockList chunkify(File src) throws IOException;

	InputStream readBlock(Block from) throws IOException;
}
