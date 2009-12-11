package edu.berkeley.grippus.map;

import java.nio.ByteBuffer;

public class Identity extends FileMapper {

	@Override
	public ByteBuffer transform(ByteBuffer in) {
		return in;
	}

	@Override
	protected int getChunkSize() {
		return 8192;
	}

}
