package edu.berkeley.grippus.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import jline.ConsoleReader;

import org.apache.log4j.BasicConfigurator;
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
		BasicConfigurator.configure();
		
		try {
			ConsoleReader console = new ConsoleReader();

			Socket sock = new Socket("localhost", Integer.parseInt(console.readLine("Port: ")));
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			out.writeObject(console.readLine("Cluster password: ", '*'));
			ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
			
			while(true) {
				String line = console.readLine("> ");
				if (line == null || line.isEmpty()) break;
				String[] cmd = line.split("\\s+");
				Class<?> c;
				try {
					c = Class.forName(Client.class.getPackage()+".command."+cmd[0]);
				} catch (ClassNotFoundException e) {
					logger.error("No such command "+cmd[0], e);
					continue;
				}
				Command command;
				try {
					Object command_ = c.newInstance();
					if (command_ instanceof Command) {
						command = (Command) command_;
					} else {
						logger.error("Not a command?" + c);
						continue;
					}
				} catch (Exception e) {
					logger.error("Could not instantiate command instance", e);
					continue;
				}
				out.writeObject(command);
				try {
					System.out.println(in.readObject());
				} catch (ClassNotFoundException e) {
					logger.error("Could not interpret result", e);
				}
			}
		} catch (IOException e) {
			logger.error("I/O error", e);
		}
	}
}
