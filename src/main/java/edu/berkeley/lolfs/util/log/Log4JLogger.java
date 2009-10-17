package edu.berkeley.lolfs.util.log;

import org.apache.log4j.Logger;

import edu.berkeley.lolfs.util.Logging;

public class Log4JLogger implements Logging {

	public Logger getLogger(Class<?> klass) {
		return Logger.getLogger(klass.getName()); // TODO: use log4j
	}

}
