package edu.berkeley.grippus.server;

import java.io.File;

import org.apache.log4j.Logger;

import edu.berkeley.grippus.util.FileSize;

public class BackingStore {
	private final long maxStoreSize;
	private final Logger logger;
	public BackingStore(Node node, File root) {
		logger = Logger.getLogger(BackingStore.class);
		if (!root.exists()) root.mkdir();
		if (!root.isDirectory())
			throw new RuntimeException("Store root " + root + " is not a directory :(");
		maxStoreSize = FileSize.parseSize(node.getConf().getString("store.maxsize"));
		logger.debug("Using up to " + maxStoreSize + " bytes for storage...");
	}
}
