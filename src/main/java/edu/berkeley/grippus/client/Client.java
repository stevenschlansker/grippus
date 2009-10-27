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
			NodeManagementRPC node = (NodeManagementRPC) factory.create(NodeManagementRPC.class, url);
			
			while(true) {
				String line = console.readLine("> ");
				if (line == null || line.isEmpty()) break;
				String[] cmd = line.split("\\s+");
				System.out.println(node.version());
			}
			
			System.out.println();
		} catch (IOException e) {
			logger.error("I/O error", e);
		}
	}
}
