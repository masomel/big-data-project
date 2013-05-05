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

    // TODO: move to Proxy class
    /** Given the list of all web data chunks and the list of the needed fingerprints,
     * create a list of chunks to be sent over to the mobile device. A null entry indicates that
     * the mobile device already has this chunk in its cache.
     */
    public ArrayList<Chunk> prepareData(ArrayList<Chunk> content, ArrayList<Integer> neededFps){

	ArrayList<Chunk> prepData = new ArrayList<Chunk>();

	if(content.size() != neededFps.size()){
	    System.out.println("Content and neededFps are not of the same length!");
	    return null;
	}

	for(int i = 0; i < content.size(); i++) {

	     // check first to see if mobile device needs this chunk
	    if(neededFps.get(i) == null){
		prepData.add(null);
	    }
	    else{
		int curFp = neededFps.get(i);
		// check to see if we already have this chunk in our cache and if the mobile device needs it
		if(cache.containsKey(curFp)){
		    prepData.add((Chunk)cache.get(curFp));
		}
		else if(!cache.containsKey(curFp)){
		    prepData.add((Chunk)content.get(i));
		}
	    }
	   
	    
	}

	return prepData;

    } //ends prepareData()
    
}