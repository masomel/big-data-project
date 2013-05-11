package processing;

import java.util.ArrayList;
import java.util.List;

import caching.ICache;
import chunking.Chunk;

public interface IProcessor {

	/**
	 * 
	 * @param content
	 * @param cache
	 */
	public void processWebContent(ArrayList<Integer> fps, List<Chunk> content, ICache cache);
	
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
