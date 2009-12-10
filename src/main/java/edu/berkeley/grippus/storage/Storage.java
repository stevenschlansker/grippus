package edu.berkeley.grippus.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public interface Storage {
	BlockList chunkify(File src) throws IOException;

	InputStream readBlock(Block from) throws IOException;
	
	void createFile(byte[] digest, byte[] fileData) throws IOException;
	
	boolean isBlockLocal(Block from);
	
	public void downloadBlock(Block from) throws FileNotFoundException;

	void propogateBlockDownload(Block b);
}
