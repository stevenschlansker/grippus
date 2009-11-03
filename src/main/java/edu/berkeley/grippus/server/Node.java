package edu.berkeley.grippus.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.UUID;
import java.util.HashMap;

import jline.ConsoleReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.security.Constraint;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.VFS;
import edu.berkeley.grippus.util.Logging;
import edu.berkeley.grippus.util.log.Log4JLogger;

public class Node {
	private enum NodeState { DISCONNECTED, OFFLINE, SLAVE, MASTER }

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
	private NodeRPC master;
	private String ipAddress;
	private String myNodeURL;

	private final VFS vfs = new VFS();
	private final HessianProxyFactory factory = new HessianProxyFactory();

	private final HashMap<String, NodeRPC> clusterMembers = new HashMap<String, NodeRPC>();
	
	private NodeRPC masterServer = null;
	private String masterURL = null;

	private static Node thisNode;

	private NodeState state = NodeState.DISCONNECTED;
	private String clusterPassword;

	public Node(String name) {
		if (thisNode != null)
			throw new RuntimeException("Already made a node here... static for now (I know this is bad!)");
		thisNode = this;
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
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
	    	this.setIpAddress(thisIp.getHostAddress());
	    	this.myNodeURL = "http://"+ getIpAddress()+":"+getPort()+"/node";
		} catch (UnknownHostException e) {
			logger.error("Unknown host", e);
			throw new RuntimeException("Node initialization fails", e);
		}
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
	
	public Boolean isMaster() {
		if (state == NodeState.MASTER) {
			return true;
		}
		return false;
	}

	public String status() {
		String result = "Node " + name + ": " + state + "\n";
		if (state == NodeState.SLAVE || state == NodeState.MASTER)
			result += "Member of: " + clusterName + " (" + clusterID + ")\n";
		if (state == NodeState.MASTER)
			result += "Advertise url: "+this.myNodeURL;
		if (state == NodeState.SLAVE || state == NodeState.MASTER) {
			result += "Cluster members:\n";
			for (String name : getClusterMembers().keySet())
				result += "\t" + name + "\n";
		}
		return result;
	}

	public synchronized boolean initCluster(String clusterName) {
		disconnect();
		state = NodeState.MASTER;
		try{
		master = (NodeRPC) factory.create(NodeRPC.class, "http://"+ getIpAddress()+":"+getPort()+"/node");
		} catch( MalformedURLException e){
			logger.error("Nodes own URL does not work as a valid URL for making the master node");
		}
		this.setClusterName(clusterName);
		setClusterID(UUID.randomUUID());
		return true;
	}
	
	/** Contacts the master node if it exists and removes self from the
	 * canonical cluster member list. Sets master to null, clears the local
	 * cluster list and sets the state to DISCONNECTED.
	 */
	public synchronized void disconnect() {
		if(master!= null){
			master.leaveCluster(this.myNodeURL);
		}
		master = null;
		masterURL = null;
		setClusterID(null);
		setClusterName(null);
		getClusterMembers().clear();
		state = NodeState.DISCONNECTED;
	}

	public HashMap<String, NodeRPC> getClusterMembers() {
		return clusterMembers;
	}

	public void setMasterServer(NodeRPC masterServer) {
		this.masterServer = masterServer;
	}

	public NodeRPC getMasterServer() {
		return masterServer;
	}

	public void setMasterURL(String masterURL) {
		this.masterURL = masterURL;
	}

	public String getMasterURL() {
		return masterURL;
	}

	public void setClusterID(UUID clusterID) {
		this.clusterID = clusterID;
	}

	public String getClusterID() {
		return clusterID.toString();
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getClusterName() {
		return clusterName;
	}

	public int getPort() {
		return port;
	}

	public VFS getVFS() {
		return vfs;
	}

	/** Method creates a NodeRPC based on the given url, and contacts it for 
	 * its master NodeRPC. The Master is then sent a join request, and this 
	 * nodes clusterSet and master are updated accordingly.
	 * 
	 * Cannot be called if Node is currently a master.
	 * 
	 * @param url
	 * @throws MalformedURLException 
	 */
	public synchronized void joinNode(String url) throws MalformedURLException{
		if( state == NodeState.MASTER) {
			logger.warn("cannot join another network if master");
			return;
		}
		NodeRPC target =  (NodeRPC) factory.create(NodeRPC.class, url);
		String masterURL = target.getMaster();
		disconnect();
		master = (NodeRPC) factory.create(NodeRPC.class, masterURL);
		master.joinCluster("http://"+ getIpAddress()+":"+getPort()+"/node");
		HashSet<String> members = master.getClusterList();
		for( String member: members){
			clusterMembers.put(member, (NodeRPC) factory.create(NodeRPC.class, member));
		}
		state = NodeState.SLAVE;
	}
	
	/** Asks the master for the canonical cluster member list and checks it against
	 *  our own; removes any excess and adds any unlisted.
	 */
	public void checkClusterMembers(){
		HashSet<String> masterMembers = master.getClusterList();
		for(String key : clusterMembers.keySet()){
			if(!masterMembers.contains(key)){
				clusterMembers.remove(key);
			}
		}
		try {
			for(String m_key : masterMembers){
				if(!clusterMembers.containsKey(m_key)){				
						clusterMembers.put(m_key, (NodeRPC) factory.create(NodeRPC.class, m_key));				
				}
			}
		} catch (MalformedURLException e) {
			logger.error("badly formed URL", e);
		}
	}
	
	public static Node getNode() {
		return thisNode;
	}
	
	public NodeRPC getMaster(){
		return master;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public Errno connectToServer(String masterServerURL, String clusterPassword) {
		conf.set("cluster.password", clusterPassword);
		this.clusterPassword = clusterPassword;
		try {
			HessianProxyFactory factory = new HessianProxyFactory();	
			factory.setUser("grippus");
			factory.setPassword(clusterPassword);
			NodeRPC master = (NodeRPC) factory.create(NodeRPC.class, masterServerURL);
			this.setMasterServer(master);
			this.setMasterURL(masterServerURL);
			this.setClusterName(master.getMasterClusterName());
			String clusterUUIDString = master.getMasterClusterUUID();
			UUID clusterID = UUID.fromString(clusterUUIDString);
			this.setClusterID(clusterID);
			String nodeURL = "http://"+this.getIpAddress()+":"+String.valueOf(this.getPort())+"/node";
			this.getMasterServer().getNewNode(nodeURL);
		} catch (MalformedURLException e) {
			logger.error("Malformed URL exception with master server url");
		}
		return Errno.SUCCESS_TOPOLOGY_CHANGE;
	}

	public void getNewNode(String newNodeURL) {
		try {
			HessianProxyFactory factory = new HessianProxyFactory();
			factory.setUser("grippus");
			factory.setPassword(clusterPassword);
			NodeRPC newNode = (NodeRPC) factory.create(NodeRPC.class,newNodeURL);
			if (this.isMaster()) {
				for (String nodeURL : this.getClusterMembers().keySet()) {
					this.clusterMembers.get(nodeURL).getNewNode(newNodeURL);
					newNode.getNewNode(nodeURL);
				}
			}
			this.clusterMembers.put(newNodeURL,newNode);
		} catch (MalformedURLException e) {
			logger.error("Malformed URL exception for new node URL");
		}
	}
}
