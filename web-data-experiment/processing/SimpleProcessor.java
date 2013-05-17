package processing;

import java.util.List;

import caching.ICache;
import chunking.Chunk;
import fingerprinting.Fingerprinting;

public class SimpleProcessor implements IProcessor {

    private int misses;
    private int hits;
    private long total_size; // sum of the sizes of all processed contents
    private double missrate;
    private int MRU;

    public SimpleProcessor() {
	this.misses = 0;
	this.hits = 0;
	this.total_size = 0;
	this.missrate = 0;
    }

    /** @params Chunked content of the webpage in ArrayList of chunks
	@return the missrate
	Simulating CACHE: 
	In this SIMPLIFIED IMPLEMENTATION, purpose: gather statistics
	Given webpage content from a Web Server (in chunked format),
	1. Determine FP of chunks
	2. If FP IS NOT in cache:
	a. add FP and chunk to cache.
	b. increment missrate
	3. If FP IS in cache:
	a. evict some chunk
	b. add FP and chunk to cache
    */
    
    @Override
	public void processWebContent(List<Chunk> content, ICache cache) {
	misses = 0;
	hits = 0;
        // For each chunk in the list, check to see if it's in the cache
        for (Chunk chunk : content) {
            if (chunk == null) {
                continue;
            }
	    
            int fp = Fingerprinting.fingerprint(chunk.getData());
	   
	    cache.put(fp, chunk);

	    if(cache.isMiss()){
		misses++;
	    }
	    else{
		hits++;
	    }
	    
	    /*  if (cache.get(fp) == null) {
            	cache.put(fp, chunk);
            	misses++;
		}
		else {
            	hits++;
		}*/
	}
	total_size = content.size();
        if (total_size != 0) {
	    missrate = ((double)misses / (double)total_size) * 100;
	}
    }
    
    @Override
	public double getMissRate() {
	//		if (total_size != 0) {
	//			return ((double)misses / (double)total_size) * 100;
	//		}
	//		return 0;
	return missrate;
    }
    
    @Override
	public double getHitRate() {
	if (total_size != 0) {
	    return ((double)hits / (double)total_size) * 100;
	}
	return 0;
    }
}
