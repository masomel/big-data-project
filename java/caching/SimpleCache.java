package caching;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import chunking.Chunk;
import fingerprinting.Fingerprinting;

 /**
  * Sample: Prototype deduplication with relatively small amounts of data
	small enough to be stored in main memory.
	
  *	Main purpose: Simulate Proxy caching and determine content overlap 
	within a website as well as between websites 
	
	MODEL:  |-------------|
			|FP1 | chunk1 |  
			|----|--------|
			|FP2 | chunk2 | 
			|----|--------|
			|FP3 | chunk3 | 
			|-------------|
			
  * What we want to be able to model soon:
   	    Given entire webpage from Web Server (in chunked format),
	    1. Determine FP of chunks
	    2. If FP IS NOT in cache: 
	    	a. add FP and chunk to cache.
	    	b. add chunk to diff. 
	    3. If FP IS in cache: 
	    	a. If FP IS NOT in phone FP:
	    		i. add chunk to diff.
	    	b. If FP IS in phone FP:
	    		ii. add FP to diff.

 * @author Madhuvanthi Jayakumar, Marcela Melara, Nayden Nedev
 */
public class SimpleCache implements ICache {

    private final int size; // Note: in number of chunks!
    private int capacity; // Needed for evictions
    private int MRU; // Most recently used item in cache
    private double missrate;

    private Map<Integer, Chunk> cache;

    public SimpleCache(int capacity) {
        this.cache = new HashMap<Integer,Chunk>();
        this.capacity = capacity;
        this.size = capacity;
        this.missrate = 0;
    }

    @Override
    public double getMissRate() {
        return missrate;
    }

    @Override
    public int getSize(){
        return size;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public Chunk get(int fp) {
        return cache.get(fp);
    }

    @Override
    public void put(int fp, Chunk c) {
        cache.put(fp, c);
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
    public void processWebContent(ArrayList<Chunk> content){
	int misses = 0;
	int hits = 0;

	// For each chunk in the list, check to see if it's in the cache
	for(Chunk chunk : content){
	    if(chunk == null){
		continue; // Don't even consider chunk when it is null
	    }

	    int fp = Fingerprinting.fingerprint(chunk.getData());

	    // Not in cache. If we have capacity, simply add, otherwise, evict.
	    if(!cache.containsKey(fp)){
		if(capacity > 0){
		    cache.put(fp, chunk);
		    misses++;
		    capacity--;
		}	    
		else{
		    //Evict most recently used item
		    cache.remove(MRU);		    
		    cache.put(fp, chunk);
		    misses++;
		}
	    }
	    else{
		hits++;
		MRU = fp;
	    }

	}

	int tsize = content.size();

	if(tsize != 0){
	    missrate = ((double)misses/(double)tsize)*100;
	}

	//set size of cache, LRU? FIFO? LIFO?
    }   
}