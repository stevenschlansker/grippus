package edu.berkeley.grippus.server;

import edu.berkeley.grippus.Errno;
import edu.berkeley.grippus.fs.DFileSpec;
import edu.berkeley.grippus.util.Pair;

public interface NodeManagementRPC {
	public String version(String cmd);
	public String terminate(String cmd);
	public String status(String cmd);
	public Errno initCluster(String cmd, String clusterName);
	public String disconnect(String cmd);
	public String ls(String cmd, DFileSpec path);
	public Errno joinCluster(String cmd, String masterURL, String clusterPassword);
	public Errno mkdir(String cmd, DFileSpec dir);
	public DFileSpec canonicalizePath(DFileSpec append);
	public Errno mount(String cmd, String realPath, String vPath);
	public Errno share(String cmd, String realPath, String vPath);
	public Pair<Errno, String> cat(String cmd, DFileSpec dFileSpec);
}
