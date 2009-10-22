package edu.berkeley.grippus.client;

import java.io.IOException;

import jline.ConsoleReader;

import org.apache.log4j.Logger;

import edu.berkeley.grippus.util.Logging;
import edu.berkeley.grippus.util.log.Log4JLogger;

public class Client {
	public final Logging log = new Log4JLogger();
	private final Logger logger = log.getLogger(Client.class);
	public static void main(String[] args) {
		new Client().run();
	}
	
	private void run() {
		boolean running = true;
		try {
			ConsoleReader console = new ConsoleReader();
			while(running) {
				String line = console.readLine("> ");
				if (line.isEmpty()) running = false;
				String[] cmd = line.split("\\s+");
			}
		} catch (IOException e) {
			logger.error("I/O error from terminal");
		}
	}
}
