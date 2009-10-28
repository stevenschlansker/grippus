package edu.berkeley.grippus.client;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import jline.ConsoleReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.berkeley.grippus.Result;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.server.NodeManagementRPC;
import edu.berkeley.grippus.util.Logging;
import edu.berkeley.grippus.util.log.Log4JLogger;

public class Client {
	public final Logging log = new Log4JLogger();
	private final Logger logger = log.getLogger(Client.class);
	private NodeManagementRPC node;
	
	private DFileSpec cwd = DFileSpec.ROOT;
	
	public static void main(String[] args) {
		new Client().run();
	}
	
	private void run() {
		BasicConfigurator.configure();
		
		try {
			ConsoleReader console = new ConsoleReader();

			String port = console.readLine("Port: ");
			String pw = console.readLine("Cluster password: ", '*');
			
			String url = "http://localhost:"+port+"/mgmt";
			HessianProxyFactory factory = new HessianProxyFactory();
			factory.setUser("grippus");
			factory.setPassword(pw);
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
			handleResult(m.invoke(this, (Object[])params));
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
		if (Result.SUCCESS_TOPOLOGY_CHANGE.equals(result)) executeCommand(node, "status");
	}

	public void cd(String cmd, String dir) {
		cwd = cwd.append(dir);
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

	public Result mkdir(String cmd, String dirname) {
		return node.mkdir(cmd, cwd.append(dirname));
	}
}
