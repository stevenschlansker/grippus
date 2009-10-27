package edu.berkeley.grippus.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jline.ConsoleReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.security.Constraint;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.MappedLoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import edu.berkeley.grippus.util.Logging;
import edu.berkeley.grippus.util.log.Log4JLogger;

public class Node {

	public final Logging log = new Log4JLogger();
	private final Logger logger = log.getLogger(Node.class);
	private volatile boolean running = false;
	private final String name;
	private final File serverRoot;
	private final Configuration conf;
	private final BackingStore bs;
	private final Server jetty;
	
	public Node(String name) {
		this.name = name;
		serverRoot = new File(System.getProperty("user.home"),".grippus/"+name);
		if (!serverRoot.exists()) serverRoot.mkdirs();
		if (!serverRoot.isDirectory())
			throw new RuntimeException("Server root " + serverRoot + " is not a directory!");
		conf = new Configuration(this, new File(serverRoot, "config"));
		conf.set("node.name", name);
		maybeInitializeConfig(conf);

		bs = new BackingStore(this, new File(serverRoot, "store"));
		System.setProperty("org.eclipse.jetty.util.log.DEBUG", "true");
		jetty = new Server(Integer.parseInt(conf.getString("node.port", "11110")));
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		if (args.length < 1 || args[0] == null) {
			System.err.println("You must supply a node instance name on the command line");
			System.exit(1);
		}
		new Node(args[0]).run();
	}
	
	public void run() {
		logger.info("Server starting up...");
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(NodeRPCImpl.class, "/node/*");
		NodeManagementRPCImpl.managedNode = this;
		context.addServlet(NodeManagementRPCImpl.class, "/mgmt/*");
		jetty.setHandler(context);
		
		try {
			jetty.start();
		} catch(Exception e) {
			logger.error("Could not start jetty", e);
		}
		
		running = true;

		while(running) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { /* don't bother */ }
		}
		
		try {
			jetty.stop();
		} catch(Exception e) {
			logger.error("Could not stop jetty", e);
		}

		logger.info("Server shutting down...");
		logger.info("Server exiting!");
	}

	public Configuration getConf() {
		return conf;
	}
	
	private void maybeInitializeConfig(Configuration conf) {
		ConsoleReader inp;
		try {
			inp = new ConsoleReader();
			maybeInitialize(conf, inp, "node.name", "Node name: ");
			maybeInitialize(conf, inp, "node.port", "Node port [11110]: ");
			maybeInitialize(conf, inp, "node.mgmtport", "Node management port [11111]: ");
			maybeInitialize(conf, inp, "store.maxsize", "Maximum size: ");
			//maybeInitialize(conf, inp, "cluster.salt", "Cluster salt: ");
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

	public BackingStore getBackingStore() {
		return bs;
	}

	public void terminate() {
		running = false;
	}

	public synchronized boolean addPeer(String string, int port) {
		return false;
	}
}
