package processing;

import java.util.List;

import caching.ICache;
import chunking.Chunk;

public interface IProcessor {

	/**
	 * 
	 * @param content
	 * @param cache
	 */
	public void processWebContent(List<Chunk> content, ICache cache);
	
	/**
	 * 
	 * @return
	 */
	public double getMissRate();
	
	/**
	 * 
	 * @return
	 */
	public double getHitRate();
}
