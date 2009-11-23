package edu.berkeley.grippus.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockList implements Serializable {
	private static final long serialVersionUID = 1L;
	private final List<Block> blockList = new ArrayList<Block>();
	public void append(Block block) {
		blockList.add(block);
	}

	public List<Block> getBlocks() {
		return Collections.unmodifiableList(blockList);
	}
}
