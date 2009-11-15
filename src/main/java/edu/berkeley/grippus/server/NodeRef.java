package edu.berkeley.grippus.server;

import java.io.Serializable;
import java.util.UUID;

public class NodeRef implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String nodeUUID = UUID.randomUUID().toString();

	public String getUUID() {
		return nodeUUID;
	}
	@Override
	public String toString() {
		return "<Node:"+nodeUUID+">";
	}
}
