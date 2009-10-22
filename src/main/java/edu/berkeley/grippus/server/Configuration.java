package edu.berkeley.grippus.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

public class Configuration {

	private final File file;
	private final HashMap<String, String> conf;
	private final Logger logger;
	
	public Configuration(Node node, File file) {
		logger = node.log.getLogger(Configuration.class);
		this.file = file;
		if (file.exists()) {
			HashMap<String, String> conf_in = null;
			try {
				conf_in = (HashMap<String, String>) SerializationUtils.deserialize(new FileInputStream(file));
				logger.debug("Loaded configuration from file...\n" + conf_in);
			} catch (FileNotFoundException e) {
				logger.error("Somehow file.exists() returns true but the FIS can't find it... :(");
				file.delete();
			}
			conf = conf_in;
		} else {
			conf = new HashMap<String, String>();
		}
	}
	
	public String get(String key) {
		return conf.get(key);
	}
	
	public String set(String key, String value) {
		String result = conf.put(key, value);
		try {
			SerializationUtils.serialize(conf, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("Could not write configuration file", e);
		}
		return result;
	}
}
