package edu.berkeley.grippus.storage;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockList implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Block> blockList = new ArrayList<Block>();
	
	public void append(Block block) {
		blockList.add(block);
	}

	public List<Block> getBlocks() {
		return Collections.unmodifiableList(blockList);
	}
	
	public void replaceBlock(Block b) {
		for (int i = 0; i < blockList.size(); i++) {
			Block r = blockList.get(i);
			if (MessageDigest.isEqual(r.getDigest(), b.getDigest())) {
				blockList.remove(i);
				blockList.add(i, b);
			}
		}
	}
}
