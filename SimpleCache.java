import java.util.*;

 /*
 *@author Madhuvanthi Jayakumar, Marcela Melara
 */
public class SimpleCache{

/* Sample: Prototype deduplication with relatively small amounts of data
	small enough to be stored in main memory.
	Main purpose: Simulate Proxy caching and determine content overlap 
	within a website as well as between websites 
	MODEL:  |-------------|
			|FP1 | chunk1 |  
			|----|--------|
			|FP2 | chunk2 | 
			|----|--------|
			|FP3 | chunk3 | 
			|-------------|
			
 * What we want to be able to model soon:
 * 	    Given entire webpage from Web Server (in chunked format),
	    1. Determine FP of chunks
	    2. If FP IS NOT in cache: 
	    	a. add FP and chunk to cache.
	    	b. add chunk to diff. 
	    3. If FP IS in cache: 
	    	a. If FP IS NOT in phone FP:
	    		i. add chunk to diff.
	    	b. If FP IS in phone FP:
	    		ii. add FP to diff.
 */

    private Hashtable cache;
    private double missrate;
    private final int size; // Note: in number of chunks!

    // Need global size limit for Proxy cache to make more like realistic cache
    private static final int CACHE_SIZE_BYTES = 524288; //0.5MB
    private static final int CACHE_SIZE_CHUNKS = CACHE_SIZE_BYTES/Chunking.getChunkSize();

    private int capacity; // Needed for evictions

    private int MRU; // Most recently used item in cache

    public SimpleCache(){
	cache = new Hashtable<Integer,Chunk>();
	capacity = CACHE_SIZE_CHUNKS;
	size = capacity;
	missrate=0;
	MRU = -1;
    }

    public SimpleCache(int s){
	cache = new Hashtable<Integer,Chunk>();
	capacity = s;
	size = s;
	missrate = 0;
    }
    
    public double getMissRate(){
	return missrate;
    }
    
    public Hashtable getCache(){
	return cache;
    }
    
    public int getCacheSize(){
	return CACHE_SIZE_BYTES;
    }

    public int getCacheSizeChunks(){
	return CACHE_SIZE_CHUNKS;
    }

    public int getCapacity(){
	return capacity;
    }
    
    public Chunk get(int fp){
	return (Chunk)(cache.get(fp));
    }
    
    public void set(int fp, Chunk c){
	cache.put(new Integer(fp), c);
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

	int size = content.size();

	if(size != 0){
	    missrate = ((double)misses/(double)size)*100;
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

	for(int i = 0; i < content.size(); i++){
	    
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