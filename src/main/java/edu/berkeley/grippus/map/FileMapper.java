package edu.berkeley.grippus.map;

import edu.berkeley.grippus.fs.DFile;

public interface FileMapper {
	String execute(DFile in, DFile out);
}
