package edu.berkeley.grippus.server;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.config.Join;
import com.hazelcast.config.JoinMembers;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.InstanceEvent;
import com.hazelcast.core.InstanceListener;
import com.hazelcast.core.Member;
import com.hazelcast.nio.Address;

public class NodeCluster implements InstanceListener {

	private final Node node;
	private final Configuration conf;
	private final BackingStore bs;
	private final Logger logger;
	private HazelcastInstance instance;
	
	public NodeCluster(Node node, Configuration conf, BackingStore bs) {
		this.node = node;
		this.conf = conf;
		this.bs = bs;
		logger = node.log.getLogger(Node.class);
	}

	public void connect() {
		Config c = new Config();
		NetworkConfig nc = new NetworkConfig();
		SymmetricEncryptionConfig sec = new SymmetricEncryptionConfig();
		Join j = new Join();
		JoinMembers jm = new JoinMembers();
		MulticastConfig mc = new MulticastConfig();
		sec.setEnabled(true);
		sec.setIterationCount(15);
		sec.setSalt(conf.getString("cluster.salt"));
		sec.setPassword(conf.getString("cluster.password"));
		nc.setSymmetricEncryptionConfig(sec);
		mc.setEnabled(false);
		j.setMulticastConfig(mc);
		for (Address a : conf.get("cluster.addresses", new HashSet<Address>()))
			jm.addAddress(a);
		j.setJoinMembers(jm);
		nc.setJoin(j);
		c.setPort(Integer.parseInt(conf.getString("node.port", "11110")));
		c.setPortAutoIncrement(false);
		c.setReuseAddress(true);
		c.setNetworkConfig(nc);
		instance = Hazelcast.newHazelcastInstance(c);
		instance.addInstanceListener(this);
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}

	public void disconnect() {
	}

	@Override
	public synchronized void instanceCreated(InstanceEvent ie) {
		logger.info("Member "+ie+" joined the cluster!");
	}

	@Override
	public void instanceDestroyed(InstanceEvent ie) {
		logger.info("Member "+ie+" left the cluster!");
	}
}
