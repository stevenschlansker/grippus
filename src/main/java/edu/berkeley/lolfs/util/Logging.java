package edu.berkeley.lolfs.util;

import org.apache.log4j.Logger;

public interface Logging {
	public Logger getLogger(Class<?> klass);
}
