package edu.berkeley.grippus.client.command;

import edu.berkeley.grippus.client.Command;
import edu.berkeley.grippus.server.Node;

public class Connect extends Command {
	private static final long serialVersionUID = 1L;
	@Override
	public Object execute(Node node) {
		int port;
		if (args.length == 2)
			port = 11110;
		else
			port = Integer.parseInt(args[2]);
		if (node.addPeer(args[1], port))
			return "Success";
		return "Failure; you are already part of a network!";
	}
}
