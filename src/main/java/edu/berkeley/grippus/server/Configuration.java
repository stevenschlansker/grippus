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
	private final HashMap<String, Object> conf;
	private final Logger logger;
	
	@SuppressWarnings("unchecked")
	public Configuration(Node node, File file) {
		logger = node.log.getLogger(Configuration.class);
		this.file = file;
		if (file.exists()) {
			HashMap<String, Object> conf_in = null;
			try {
				conf_in = (HashMap<String, Object>) SerializationUtils.deserialize(new FileInputStream(file));
				logger.debug("Loaded configuration from file...\n" + conf_in);
			} catch (FileNotFoundException e) {
				logger.error("Somehow file.exists() returns true but the FIS can't find it... :(");
				file.delete();
			}
			conf = conf_in;
		} else {
			conf = new HashMap<String, Object>();
		}
	}
	
	public String getString(String key) {
		Object val = conf.get(key);
		return (val == null ? null : val.toString());
	}
	
	public Object set(String key, Object value) {
		Object result = conf.put(key, value);
		try {
			SerializationUtils.serialize(conf, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("Could not write configuration file", e);
		}
		return result;
	}

	public String getString(String key, String dfl) {
		String result = getString(key);
		if (result == null)
			return dfl;
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T dfl) {
		T result = (T) conf.get(key);
		if (result == null)
			return dfl;
		return result;
	}
}
