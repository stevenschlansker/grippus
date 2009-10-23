package edu.berkeley.grippus.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import jline.ConsoleReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.berkeley.grippus.client.Command;
import edu.berkeley.grippus.client.command.Quit;
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
	private final NodeCluster cls;
	private final NodeManagementServer nms;
	
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
		cls = new NodeCluster(this);
		nms = new NodeManagementServer();
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
		
		running = true;
		cls.connect();

		while(running) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}

		logger.info("Server shutting down...");
		cls.disconnect();
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

	public BackingStore getBackingStore() {
		return bs;
	}

	private class NodeManagementServer implements Runnable {
		ServerSocket ss;
		
		private class NodeManagementServerThread implements Runnable {
			private Socket sock;
			public NodeManagementServerThread(Socket s) {
				sock = s;
			}
			@Override
			public void run() {
				try {
					ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
					ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
					String pw = in.readObject().toString();
					if (!pw.equals(conf.getString("cluster.password"))) {
						logger.warn("Bad password from "+sock);
						in.close();
						out.close();
						sock.close();
						return;
					}
					logger.info("Management connect from "+sock);
					while(running) {
						Object incoming = in.readObject();
						Command cmd = (Command) incoming;
						if (cmd instanceof Quit)
							break;
						out.writeObject(cmd.execute(Node.this));
					}
					in.close();
					out.close();
					sock.close();
				} catch (IOException e) {
					logger.warn("I/O exception from management thread", e);
				} catch (ClassNotFoundException e) {
					logger.warn("Could not deserialize object", e);
				} catch (ClassCastException e) {
					logger.warn("Client did something bad!", e);
				}
			}
		}
		
		public NodeManagementServer() {
			try {
				ss = new ServerSocket(Integer.parseInt(conf.getString("node.mgmtport", "11111")), 0, InetAddress.getByName("localhost"));
				Thread nms = new Thread(this);
				nms.setDaemon(true);
				nms.start();
			} catch (IOException e) {
				logger.error("I/O error; management interface shut down", e);
			}
		}
		@Override
		public void run() {
			while(true) {
				try {
					Socket s = ss.accept();
					new Thread(new NodeManagementServerThread(s)).start();
				} catch (IOException e) {
					logger.warn("I/O error while talking to client");
				}
			}
		}
	}

	public void terminate() {
		running = false;
	}

	public synchronized boolean addPeer(String string, int port) {
		conf.get(key, dfl)
	}
}
