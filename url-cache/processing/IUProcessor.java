package processing;

import java.util.ArrayList;
import java.util.List;

import caching.ICache;
import caching.IUCache;
import chunking.Chunk;

public interface IUProcessor {

	/**
	 * 
	 * @param content
	 * @param cache
	 */
	public void processWebContent(String url, ArrayList<Integer> fp, IUCache urlcache);
	
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
