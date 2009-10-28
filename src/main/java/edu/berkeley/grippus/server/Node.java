package edu.berkeley.grippus.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jline.ConsoleReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.security.Constraint;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
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
	private UUID clusterID;
	private String clusterName;
	private final int port;
	
	private final Set<NodeRPC> clusterMembers = new HashSet<NodeRPC>();
	
	private NodeState state = NodeState.DISCONNECTED;
	
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
		//System.setProperty("org.eclipse.jetty.util.log.DEBUG", "true");
		port = Integer.parseInt(conf.getString("node.port", "11110"));
		jetty = new Server(port);
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		if (args.length < 1 || args[0] == null || args[0].isEmpty()) {
			System.err.println("You must supply a node instance name on the command line");
			System.exit(1);
		}
		new Node(args[0]).run();
	}
	
	public void run() {
		logger.info("Server starting up...");
		
		configureJetty();
		
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

	private void configureJetty() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__BASIC_AUTH);
		constraint.setRoles(new String[] {"grippus"});
		constraint.setAuthenticate(true);
		ConstraintMapping cm = new ConstraintMapping();
		cm.setConstraint(constraint);
		cm.setPathSpec("/*");
		ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
		sh.setConstraintMappings(new ConstraintMapping[] {cm});
		File tempFile;
		try {
			tempFile = File.createTempFile("realm", "passwd");
			tempFile.deleteOnExit();
			FileWriter w = new FileWriter(tempFile);
			w.write("grippus: " + conf.getString("mgmt.password")+", grippus");
			w.close();
		} catch (IOException e) {
			logger.error("IO exception while setting up authentication: ", e);
			throw new RuntimeException("initialization failed");
		}
		sh.setLoginService(new HashLoginService("grippus", tempFile.getAbsolutePath()));
		context.setSecurityHandler(sh);
		context.setContextPath("/");
		context.addServlet(NodeRPCImpl.class, "/node/*");
		NodeManagementRPCImpl.managedNode = this;
		context.addServlet(NodeManagementRPCImpl.class, "/mgmt/*");
		jetty.setHandler(context);
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
			//maybeInitialize(conf, inp, "node.mgmtport", "Node management port [11111]: ");
			maybeInitialize(conf, inp, "store.maxsize", "Maximum size: ");
			maybeInitialize(conf, inp, "mgmt.password", "Management password: ");
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

	public synchronized void terminate() {
		disconnect();
		running = false;
	}

	public synchronized boolean addPeer(String string, int port) {
		return false;
	}

	public String status() {
		String result = "Node " + name + ": " + state + "\n";
		if (state == NodeState.SLAVE || state == NodeState.MASTER)
			result += "Member of: " + clusterName + " (" + clusterID + ")\n";
		if (state == NodeState.MASTER)
			result += "Advertise url: http://<external ip>:"+port+"/node";
		return result;
	}
	
	private enum NodeState { DISCONNECTED, OFFLINE, SLAVE, MASTER }

	public synchronized boolean initCluster(String clusterName) {
		disconnect();
		state = NodeState.MASTER;
		this.clusterName = clusterName;
		clusterID = UUID.randomUUID();
		return true;
	}

	public synchronized void disconnect() {
		clusterID = null;
		clusterName = null;
		clusterMembers.clear();
		state = NodeState.DISCONNECTED;
	}
}
