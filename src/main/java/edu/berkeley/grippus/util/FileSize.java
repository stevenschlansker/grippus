package edu.berkeley.grippus.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSize {

	private static final Pattern parser = Pattern.compile("\\s*(\\d+)\\s*(([gGmMkKtT])[iI]?[bB]?)?\\s*");
	public static long parseSize(String sizeStr) {
		Matcher m = parser.matcher(sizeStr);
		if (!m.matches())
			throw new IllegalArgumentException("Bad size "+sizeStr);
		long size = Long.parseLong(m.group(1));
		switch(m.group(3).charAt(0)) {
		case 't':
		case 'T':
			size *= 1024;
			//$FALL-THROUGH$
		case 'g':
		case 'G':
			size *= 1024;
			//$FALL-THROUGH$
		case 'm':
		case 'M':
			size *= 1024;
			//$FALL-THROUGH$
		case 'k':
		case 'K':
			size *= 1024;
		}
		return size;
	}

}
