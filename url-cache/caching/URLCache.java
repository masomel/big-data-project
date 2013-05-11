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
			


* @author Madhuvanthi Jayakumar, Marcela Melara, Nayden Nedev
*/
public class URLCache implements IUCache {

    private final int size; // Note: in number of chunks!
    private int capacity; // Needed for evictions
    private boolean isMiss; //Needed for statistical purposes
    private String MRU; // Most recently used item in cache

    private Map<String, ArrayList<Integer>> cache;
    
    public URLCache(int capacity) {
        this.cache = new HashMap<String, ArrayList<Integer>>();
        this.capacity = capacity;
        this.size = capacity;
	this.isMiss = false;
    }
    
    @Override
	public int getSize(){
        return size;
    }
    
    @Override
	public int getCapacity() {
        return capacity;
    }
    
    public ArrayList<Integer> remove(String url){
    	return cache.remove(url);
    }
    
    @Override
	public ArrayList<Integer> get(String url) {
        return cache.get(url);
    }
    
    @Override
	public boolean isMiss(){
	return isMiss;
    }
    
    @Override
	public void put(String url, ArrayList<Integer> fps) {
		isMiss = false;
		if(!cache.containsKey(url)){
			isMiss = true;
			cache.put(url, fps);
			capacity = capacity - (fps.size());
		}

	/*
        if (!cache.containsKey(url)) {	    
	        if (capacity > 0) {
	                cache.put(url, fps);
	                capacity--;  		
		    }
		    else {
				// Evict most recently used item
				cache.remove(MRU);
				cache.put(url, fps);
		    }
			    isMiss = true;
        }
        else {
            MRU = url;        	
        }	
        System.out.println("capacity" + capacity);
    */
    }
   
}
