import java.util.ArrayList;
import java.util.Hashtable;


 /*
 *@author Madhuvanthi Jayakumar
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

	public SimpleCache(){
		cache = new Hashtable<Integer,Chunk>();
		missrate=0;

	}

	public double getMissRate(){
		return missrate;
	}

	public Hashtable getCache(){
		return cache;
	}
	
	public Chunk get(int fp){
		return (Chunk)(cache.get(fp));
	}
	
	public void set(int fp, Chunk c){
		cache.put(new Integer(fp), c);
	}

	/** @params: chunked content of the webpage in byte array
	    @return: int array with content
	    Simulating PROXY CACHE: 
	 	In this SIMPLIFIED IMPLEMENTATION, purpose: gather statistics
	 	Given entire webpage from Web Server (in chunked format),
	    1. Determine FP of chunks
	    2. If FP IS NOT in cache:
	    	a. add FP and chunk to cache.
	    	b. add FP to diff.
	    	c. increment missrate
	    	d. consider eviction?
	    3. If FP IS in cache:
	    	a. add FP to diff
	     */
	public ArrayList getWebContent(Chunk[] content){
		missrate=0;
		int len = content.length;
		ArrayList<Integer> diff = new ArrayList<Integer>();
		int fp;
		for(int i= 0; i<len; i++){
			fp = FingerPrinting.fingerprint(content[i].getData());
			diff.add(fp);	
			if(!cache.containsKey(fp)){
				cache.put(fp, content[i]);
				missrate+=1;
			}
		}
		missrate = missrate/content.length;
		return diff;
		
		//set size of cache, LRU? FIFO? LIFO?
	}
	
	/** Overloading with Arraylist of Chunks instead of Array of Chunks */
	public ArrayList getWebContent(ArrayList content){
		missrate=0;
		int len = content.size();
		ArrayList<Integer> diff = new ArrayList<Integer>();
		int fp;
		for(int i= 0; i<len; i++){
			fp = FingerPrinting.fingerprint(content.get(i).getData());
			diff.add(fp);	
			if(!cache.containsKey(fp)){
				cache.put(fp, content.get(i));
				missrate+=1;
			}
		}
		missrate = missrate/content.length;
		return diff;
		
		//set size of cache, LRU? FIFO? LIFO?
	}


}