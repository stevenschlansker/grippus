package edu.berkeley.grippus.server;

public interface NodeManagementRPC {

	public String version(String cmd);
	public String terminate(String cmd);

}
