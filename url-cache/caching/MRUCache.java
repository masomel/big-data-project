package caching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import processing.URLProcessor;

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
public class MRUCache implements ICache {

    private final int size; // Note: in number of chunks!
    private int capacity; // Needed for evictions
    private boolean isMiss; //Needed for statistical purposes
    private int MRU; // Most recently used item in cache
    private int counter; 
    private Map<Integer, Chunk> cache;
    private int i;
    private ArrayList<Integer> Evictfps;
    
    public MRUCache(int capacity) {
        this.cache = new HashMap<Integer,Chunk>();
        this.capacity = capacity;
        this.size = capacity;
        this.isMiss = false;
        this.counter=0;
        this.i=0;
        this.Evictfps = new ArrayList<Integer>();
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
    
    public Chunk remove(int fp){
    	return cache.remove(fp);
    }
    
    @Override
	public boolean isMiss(){
	return isMiss;
    }
    
    @Override
	public void put(int fp, Chunk c) {
    	
		isMiss = false;
		 
		if(!cache.containsKey(fp)){
			isMiss = true;
			cache.put(fp,c);
			capacity= capacity-1;
			//System.out.println("eviction..");
			if(capacity<=0 && counter == 0){
				System.out.println("CAPACITY NOT ENOUGH!!!");
				Evictfps = URLProcessor.getEvictingFPs();
				counter++;
			//	System.out.println("size: " + this.getSize());
			//	System.out.println(cache.get(Evictfps.get(0)) + " ");
			//	System.out.println(cache.get(Evictfps.get(1)) + " ");
			}
			/*
			for(int j=0; j<Evictfps.size(); j++){
				System.out.print(Evictfps.get(j) + " ");
				System.out.println();
				System.out.print(this.get(Evictfps.get(j)) + " ");
			}
			*/

			if(capacity<=0 && counter!=0){
			//	System.out.print(i + " ");
			//	System.out.println(cache.get(Evictfps.get(i)));
				cache.remove(Evictfps.get(i));
				capacity= capacity+1;
				i++;
				
				if(i==Evictfps.size()){
					System.out.println("RESET");
					counter=0;
					i=0;
				}
			}
		}
	/*
        if (!cache.containsKey(fp)) {
	    if (capacity > 0) {
                cache.put(fp, c);
		capacity--;  		
	    }
	    else {
		// Evict most recently used item
		cache.remove(MRU);
		cache.put(fp, c);
	    }
	    isMiss = true;
        }
        else {
            MRU = fp;        	
        }	
        */
    }
}
