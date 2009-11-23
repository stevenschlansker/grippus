package edu.berkeley.grippus.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import edu.berkeley.grippus.server.Node;

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
	
	

}
