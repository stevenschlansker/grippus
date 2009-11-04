package edu.berkeley.grippus.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.caucho.hessian.server.HessianServlet;

public class NodeMasterRPCImpl extends HessianServlet implements NodeMasterRPC {
	private static final long serialVersionUID = 1L;

	private Node myNode;
	private Logger logger;

	public NodeMasterRPCImpl() { /* nothing */ }

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		myNode = (Node) config.getServletContext().getAttribute("node");
		logger = myNode.log.getLogger(NodeRPCImpl.class);
	}
}
