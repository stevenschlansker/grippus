package edu.berkeley.grippus.util;

import org.apache.log4j.Logger;

public abstract class Periodic implements Runnable {
	private static final Logger LOG = Logger.getLogger(Periodic.class);
	private final Thread runner = new Thread(this);
	private final long sleep;
	private boolean running = true;
	public Periodic(long sleep, String name) {
		this.sleep = sleep;
		runner.setName(name);
		runner.start();
	}
	protected abstract void fire();
	@Override
	public void run() {
		while(running) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				LOG.error("Someone interrupted my precious \""+runner.getName()+"\" thread, dying!", e);
				return;
			} catch (RuntimeException r) {
				LOG.error("Unexpected exception", r);
			}
			fire();
		}
	}
	
	protected void die() {
		running = false;
	}
}
