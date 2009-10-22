package edu.berkeley.grippus.server;

import org.apache.log4j.Logger;

import edu.berkeley.grippus.util.Logging;
import edu.berkeley.grippus.util.log.Log4JLogger;

public class Server {
	public final Logging log = new Log4JLogger();
	private final Logger logger = log.getLogger(Server.class);
	
	public static void main(String[] args) {
		new Server().run();
	}
	
	public void run() {
		logger.info("Server starting up...");
		logger.info("Server shutting down...");
		logger.info("Server exiting!");
	}
}
