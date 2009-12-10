package edu.berkeley.grippus.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

	private Queue<InputStream> streams;
	private InputStream current;

	public BlockListInputStream(final Storage s, BlockList data) {
		// Make a thread pool
		// Have a job-queue, take a thread and utilize it.
		// if it fails, throw an exception and log an error. 
		// isntead of trying to return input streams.
		// just make a series of input streams.
		
		streams = new LinkedList<InputStream>();
		ConcurrentLinkedQueue<Block> validBlocks = new ConcurrentLinkedQueue<Block>(data.getBlocks());
		if (!s.isBlockLocal(validBlocks.peek())) {
			class BlockFetcher implements Runnable {
				ConcurrentLinkedQueue<Block> queue;
				Storage s;
				
				public BlockFetcher(ConcurrentLinkedQueue<Block> validBlocks, Storage s) {
					this.queue = validBlocks;
					this.s = s;
				}
				
				@Override
				public void run() {
					while (!this.queue.isEmpty()) {
						Block b = this.queue.poll();
						try {
							s.downloadBlock(b);
							s.propogateBlockDownload(b);
							Thread.sleep(5000);
						} catch (IOException e) {
							this.queue.offer(b);
						} catch (InterruptedException e) {
							LOG.error("Interrupted exception");
						}
					} 
				}
			}
			
			ExecutorService threadExecutor = Executors.newFixedThreadPool(5);
			
			for (int i = 0; i < validBlocks.size(); i++) {
				BlockFetcher bf = new BlockFetcher(validBlocks,s);
				threadExecutor.execute(bf);
			}
			
			threadExecutor.shutdown();
			try {
				threadExecutor.awaitTermination(1, TimeUnit.DAYS);
			} catch (InterruptedException e1) {
				LOG.error("Interrupt exception is bad mojo dojo");
			}
		}
		
		List<Block> blocks = data.getBlocks();
		for (int i = 0; i < data.getBlocks().size(); i++) {
			try {
				Block b = blocks.get(i);
				InputStream stream = s.readBlock(b);
				streams.add(s.readBlock(b));
			} catch (IOException e) {
				streams.add(new CorruptedBlockInputStream(blocks.get(i).length));
			}
		}
		
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
