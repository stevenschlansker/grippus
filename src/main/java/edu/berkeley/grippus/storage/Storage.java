package edu.berkeley.grippus.storage;

import java.io.File;
import java.io.IOException;


public interface Storage {
	BlockList chunkify(File src) throws IOException;
}
