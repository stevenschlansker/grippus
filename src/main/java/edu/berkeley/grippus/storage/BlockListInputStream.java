package edu.berkeley.grippus.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class BlockListInputStream extends InputStream {
	private static final Logger LOG = Logger.getLogger(BlockListInputStream.class);
	public class CorruptedBlockInputStream extends InputStream {
		private int len;

		public CorruptedBlockInputStream(int length) {
			len = length;
		}

		@Override
		public int read() throws IOException {
			if (len-- > 0)
				return 0;
			return -1;
		}
	}

	private final Queue<InputStream> streams;
	private InputStream current;

	public BlockListInputStream(final Storage s, BlockList data) {
		
		streams = new LinkedList<InputStream>(Collections2.transform(data.getBlocks(),
				new Function<Block, InputStream>() {
			@Override public InputStream apply(Block from) {
				try {
					return s.readBlock(from);
				} catch (IOException e) {
					LOG.error("Could not open block " + from
							+ ", file is corrupted!", e);
					return new CorruptedBlockInputStream(from.length);
				}
			}
		}));
		current = streams.poll();
	}
	

	@Override
	public int read() throws IOException {
		if (current == null)
			return -1;
		int i;
		if ((i = current.read()) != -1)
			return i;
		current.close();
		current = streams.poll();
		return read();
	}
}
