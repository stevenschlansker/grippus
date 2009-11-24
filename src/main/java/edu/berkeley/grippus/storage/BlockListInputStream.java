package edu.berkeley.grippus.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
		ArrayList<String> validNodes = (ArrayList) Collections.synchronizedCollection(data.getBlocks().get(0).remoteNodes);
		int numBlocks = data.getBlocks().size();
		ArrayList<Block> droppedBlocks = new ArrayList<Block>();
		droppedBlocks = (ArrayList) Collections.synchronizedList(droppedBlocks);
		int numValidNodes = validNodes.size();
		ArrayList<InputStream> blockStreams = (ArrayList) Collections.synchronizedCollection(new ArrayList<InputStream>());
		for (int i = 0; i < numBlocks; i++) {
			blockStreams.add(null);
		}
		//Based on the # of nodes, you want to split the blocks evenly
		//Create a new thread for each node and have it work on a set of blocks
		//Any dropped blocks need to be added to the droppedFiles list
		//Repeat process on the dropped block list randomizing which nodes gets access?
//		class BasicThread extends Thread {
//			@Override
//			public void run() {
//				
//
//			}
//			
//			String nodeURL;
//			int startIndex, endIndex;
//			
//			public void setNode(String nodeURL) {
//				this.nodeURL = nodeURL;
//			}
//			
//			public void setIndex(int start, int end) {
//				this.startIndex = start;
//				this.endIndex = end;
//			}
//			
//			public void setBlocks()
//		}
		
	
		streams = new LinkedList<InputStream>(Collections2.transform(data.getBlocks(),
				new Function<Block, InputStream>() {
			@Override public InputStream apply(Block from) {
				InputStream returnStream = null;
				class BThread extends Thread {
					@Override
					public void run() {
						try {
							this.s = storage.readBlock(b);
						} catch (IOException e) {
							LOG.error("Could not open block " + b
									+ ", file is corrupted!", e);
							this.s = new CorruptedBlockInputStream(b.length);
						}
					}
					
					Block b;
					InputStream s;
					Storage storage;
					
					public void setBlock(Block block) {
						this.b = block;
					}
					
					public void setStream(InputStream s) {
						this.s = s;
					}
					
					public void setStorage(Storage s) {
						this.storage = s;
					}
				}
				BThread th = new BThread();
				th.setBlock(from);
				th.setStream(returnStream);
				th.setStorage(s);
				th.start();
				
			  long delayMillis = 5000; // 5 seconds
			    try {
			        th.join(delayMillis);
			    
			        if (th.isAlive()) {
			            return returnStream;
			        } else {
			            // Finished
			        }
			    } catch (InterruptedException e) {
			        // Thread was interrupted
			    }

				return returnStream;
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
