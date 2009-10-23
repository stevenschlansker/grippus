package edu.berkeley.grippus.client.command;

import edu.berkeley.grippus.client.Command;
import edu.berkeley.grippus.server.Node;

public class Terminate extends Command {
	private static final long serialVersionUID = 1L;
	@Override
	public Object execute(Node node) {
		node.terminate();
		return "";
	}
}
