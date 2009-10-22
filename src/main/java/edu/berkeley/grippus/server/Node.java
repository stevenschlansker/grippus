package edu.berkeley.grippus.server;

import java.io.File;
import java.io.IOException;

import jline.ConsoleReader;

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
		Configuration conf = new Configuration(this, new File(serverRoot, "config"));
		maybeInitializeConfig(conf);
		BackingStore bs = new BackingStore(this, conf, new File(serverRoot, "store"));
		NodeCluster cls = new NodeCluster(this, conf, bs);
		
		running = true;
		cls.connect();
		cls.run();

		logger.info("Server shutting down...");
		cls.disconnect();
		logger.info("Server exiting!");
	}

	private void maybeInitializeConfig(Configuration conf) {
		ConsoleReader inp;
		try {
			inp = new ConsoleReader();
			maybeInitialize(conf, inp, "node.name", "Node name: ");
			maybeInitialize(conf, inp, "store.maxsize", "Maximum size: ");
			maybeInitialize(conf, inp, "cluster.salt", "Cluster salt: ");
			maybeInitialize(conf, inp, "cluster.password", "Cluster password: ");
		} catch (IOException e) {
			logger.error("Could not read from console; cannot configure, dying");
			throw new RuntimeException("I/O problem", e);
		}
	}
	
	private void maybeInitialize(Configuration conf, ConsoleReader inp, String key, String prompt) throws IOException {
		if (conf.getString(key) == null)
			conf.set(key, inp.readLine(prompt));
	}
}
