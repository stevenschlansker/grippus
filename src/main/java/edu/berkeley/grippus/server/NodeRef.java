package edu.berkeley.grippus.server;

import java.io.Serializable;
import java.util.UUID;

public class NodeRef implements Serializable {
	private static final long serialVersionUID = 1L;
	private final UUID nodeUUID = UUID.randomUUID();
	public UUID getUUID() {
		return nodeUUID;
	}
	@Override
	public String toString() {
		return "<Node:"+nodeUUID+">";
	}
}
