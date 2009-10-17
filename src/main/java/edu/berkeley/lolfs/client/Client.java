package edu.berkeley.lolfs.client;

import org.apache.log4j.Logger;

import edu.berkeley.lolfs.util.Logging;
import edu.berkeley.lolfs.util.log.Log4JLogger;

public class Client {
	public final Logging log = new Log4JLogger();
	private final Logger logger = log.getLogger(Client.class);
	public static void main(String[] args) {
		new Client().run();
	}
	
	private void run() {
		
	}
}
