package edu.berkeley.grippus.client;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import jline.ConsoleReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.server.NodeManagementRPC;
import edu.berkeley.grippus.util.Pair;

public class Client {
	private final Logger logger = Logger.getLogger(Client.class);
	private NodeManagementRPC node;

	private DFileSpec cwd = DFileSpec.ROOT;

	public static void main(String ... args) {
		new Client().run(args);
	}

	private void run(String[] args) {
		BasicConfigurator.configure();

		try {
			ConsoleReader console = new ConsoleReader();

			String port = (args.length > 0 ? args[0] : console.readLine("Port: "));
			//String pw = console.readLine("Cluster password: ", '*');

			String url = "http://localhost:"+port+"/mgmt";
			HessianProxyFactory factory = new HessianProxyFactory();
			//factory.setUser("grippus");
			//factory.setPassword(pw);
			node = (NodeManagementRPC) factory.create(NodeManagementRPC.class, url);

			executeCommand(node, "status");

			while(true) {
				String line = console.readLine("> ");
				if (line == null || line.isEmpty()) break;
				String[] cmd = line.split("\\s+");
				if (cmd[0].equalsIgnoreCase("quit")) break;
				executeCommand(node, cmd[0], (Object[])Arrays.copyOfRange(cmd, 1, cmd.length));
				if (cmd[0].equalsIgnoreCase("terminate")) break;
			}

			System.out.println();
		} catch (IOException e) {
			logger.error("I/O error", e);
		}
	}

	private void executeCommand(NodeManagementRPC node, String cmd, Object... args) {
		Object[] params = new Object[args.length + 1];
		Class<?>[] paramsClass = new Class<?>[args.length + 1];
		params[0] = cmd;
		paramsClass[0] = String.class;
		for (int i = 0; i < args.length; i++) {
			params[i+1] = args[i];
			if (args[i] != null)
				paramsClass[i+1] = args[i].getClass();
			else
				paramsClass[i+1] = Object.class;
		}
		try {
			Method m = this.getClass().getMethod(cmd, paramsClass);
			handleResult(m.invoke(this, params));
			return;
		} catch (SecurityException e) { // try remote
		} catch (NoSuchMethodException e) { // try remote
		} catch (IllegalArgumentException e) { // try remote
		} catch (IllegalAccessException e) { // try remote
		} catch (InvocationTargetException e) { // try remote
			logger.error("Invocation target exception", e);
		}
		try {
			Method m = node.getClass().getMethod(cmd, paramsClass);
			handleResult(m.invoke(node, params));
		} catch (SecurityException e) {
			logger.error("No bitch!", e);
		} catch (NoSuchMethodException e) {
			logger.error("No such method", e);
		} catch (IllegalArgumentException e) {
			logger.error("Bad arguments", e);
		} catch (IllegalAccessException e) {
			logger.error("No bitch!", e);
		} catch (InvocationTargetException e) {
			logger.error("Invocation target exception", e);
		}
	}

	private void handleResult(Object result) {
		if (result != null) System.out.println(result);
		if (Errno.SUCCESS_TOPOLOGY_CHANGE.equals(result)) executeCommand(node, "status");
	}

	public void cd(String cmd, String dir) {
		cwd = node.canonicalizePath(cwd.append(dir));
	}

	public DFileSpec pwd(String cmd) {
		return cwd;
	}

	public String ls(String cmd) {
		return node.ls(cmd, cwd);
	}

	public String ls(String cmd, String target) {
		return node.ls(cmd, new DFileSpec(cwd + "/" + target));
	}

	public Errno mkdir(String cmd, String dirname) {
		return node.mkdir(cmd, cwd.append(dirname));
	}

	public Errno connectToNetwork(String cmd, String masterURL) {
		return node.joinCluster(cmd, masterURL, "k");
	}

	public Errno cat(String cmd, String path) {
		Pair<Errno, String> result = node.cat(cmd, cwd.append(path));
		System.out.println(result.cdr());
		return result.car();
	}

	public Errno digest(String cmd, String algo, String path) {
		Pair<Errno, String> result = node.sha1(cmd, algo, cwd.append(path));
		System.out.println(result.cdr());
		return result.car();
	}
}
