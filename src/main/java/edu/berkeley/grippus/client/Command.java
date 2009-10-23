package edu.berkeley.grippus.client;

import java.io.Serializable;

import edu.berkeley.grippus.server.Node;

public abstract class Command implements Serializable {
	private static final long serialVersionUID = 1;
	protected String[] args;

	public abstract Object execute(Node node);

	public void setArgs(String[] cmd) {
		args = cmd;
	}

}
