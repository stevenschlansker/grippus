package edu.berkeley.grippus.client.command;

import edu.berkeley.grippus.client.Command;
import edu.berkeley.grippus.server.Node;

public class Version extends Command {
	private static final long serialVersionUID = 1L;
	@Override
	public Object execute(Node node) {
		return this;
	}
	
	@Override
	public String toString() {
		return "Version!";
	}

}
