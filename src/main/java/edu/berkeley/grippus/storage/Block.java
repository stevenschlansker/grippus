package edu.berkeley.grippus.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Block implements Serializable {
	private static final long serialVersionUID = 1L;
	private final byte[] digest;
	final int length;
	ArrayList<String> remoteNodes;

	@SuppressWarnings("unused") // for Hessian
	private Block() {
		digest = null;
		length = -1;
		remoteNodes = null;
	}

	public Block(byte[] digest, int length, String nodeURL) {
		this.digest = Arrays.copyOf(digest, digest.length);
		this.length = length;
		this.remoteNodes = new ArrayList<String>();
		this.remoteNodes.add(nodeURL);
	}

	public byte[] getDigest() {
		return digest;
	}
	
	public void addNode(String nodeURL) {
		if (!this.remoteNodes.contains(nodeURL)) {
			this.remoteNodes.add(nodeURL);
		}
	}
	
	

}
