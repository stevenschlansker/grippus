package edu.berkeley.grippus.server;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.berkeley.grippus.util.Logging;
import edu.berkeley.grippus.util.log.Log4JLogger;

public class Node {
	public final Logging log = new Log4JLogger();
	private final Logger logger = log.getLogger(Node.class);
	private volatile boolean running = false;
	File serverRoot = new File(System.getProperty("user.home"),".grippus");
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		new Node().run();
	}
	
	public void run() {
		logger.info("Server starting up...");
		if (!serverRoot.exists()) serverRoot.mkdir();
		if (!serverRoot.isDirectory())
			throw new RuntimeException("Server root " + serverRoot + " is not a directory!");
		BackingStore bs = new BackingStore(new File(serverRoot, "store"));
		Configuration conf = new Configuration(new File(serverRoot, "config"));
		NodeCluster cls = new NodeCluster(conf,bs);
		
		running = true;
		cls.connect();
		cls.run();

		logger.info("Server shutting down...");
		cls.disconnect();
		logger.info("Server exiting!");
	}
}
