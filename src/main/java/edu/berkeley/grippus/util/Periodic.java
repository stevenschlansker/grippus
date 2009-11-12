package edu.berkeley.grippus.util;

import org.apache.log4j.Logger;

public abstract class Periodic implements Runnable {
	private static final Logger LOG = Logger.getLogger(Periodic.class);
	private final Thread runner = new Thread(this);
	private final long sleep;
	public Periodic(long sleep, String name) {
		this.sleep = sleep;
		runner.setName(name);
		runner.start();
	}
	protected abstract void tick();
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				LOG.error("Someone interrupted my precious \""+runner.getName()+"\" thread, dying!", e);
				return;
			}
			tick();
		}
	}
}
