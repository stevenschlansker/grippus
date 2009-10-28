package edu.berkeley.grippus.client;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jline.ConsoleReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;

import edu.berkeley.grippus.server.NodeManagementRPC;
import edu.berkeley.grippus.util.Logging;
import edu.berkeley.grippus.util.log.Log4JLogger;

public class Client {
	public final Logging log = new Log4JLogger();
	private final Logger logger = log.getLogger(Client.class);
	private NodeManagementRPC node;
	
	private String cwd = "/";
	
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
				executeCommand(node, cmd);
				if (cmd[0].equalsIgnoreCase("terminate")) break;
			}
			
			System.out.println();
		} catch (IOException e) {
			logger.error("I/O error", e);
		}
	}

	private void executeCommand(NodeManagementRPC node, String... cmd) {
		Class<?>[] params = new Class<?>[cmd.length];
		for (int i = 0; i < params.length; i++)
			params[i] = String.class;
		try {
			Method m = this.getClass().getMethod(cmd[0], params);
			Object result = m.invoke(this, (Object[])cmd);
			if (result != null) System.out.println(result);
			return;
		} catch (SecurityException e) { // try remote
		} catch (NoSuchMethodException e) { // try remote
		} catch (IllegalArgumentException e) { // try remote
		} catch (IllegalAccessException e) { // try remote
		} catch (InvocationTargetException e) { // try remote
		}
		try {
			Method m = node.getClass().getMethod(cmd[0], params);
			Object result = m.invoke(node, (Object[])cmd);
			if (result != null) System.out.println(result);
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
	
	public String pwd(String cmd) {
		return cwd;
	}
	
	public String ls(String cmd) {
		executeCommand(node, cmd, cwd);
		return null;
	}
}
