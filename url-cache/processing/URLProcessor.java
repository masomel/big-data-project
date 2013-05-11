package processing;

import java.util.ArrayList;
import java.util.List;

import caching.ICache;
import caching.IUCache;
import chunking.Chunk;
import fingerprinting.Fingerprinting;

public class URLProcessor implements IUProcessor {

    private int misses;
    private int hits;
    private long total_size; // sum of the sizes of all processed contents
    private double missrate;
    private int MRU;
    private static ArrayList<String> urlOrder;
    private static IUCache cache;
    private static int count;

    public URLProcessor() {
		this.misses = 0;
		this.hits = 0;
		this.total_size = 0;
		this.missrate = 0;
		urlOrder = new ArrayList<String>();
		count = 0;
    }
    
    public static ArrayList<String> getURLOrder(){
    	return urlOrder;
    }
    
    public void setCache(IUCache cahce){
    	this.cache = cache;
    }
    
    /** 
     * Get the list of fingerprints that are associated with the url we're evicting
     * @return
     */
    public static ArrayList<Integer> getEvictingFPs(){
    	/*
    	System.out.println("order of url:");
    	for(int i=0; i<urlOrder.size(); i++){
    		System.out.println(urlOrder.get(i));
    	}
    	*/
		
    	String evictURL = urlOrder.remove(0);
    	System.out.println("Evicting this url: " + evictURL);
    	ArrayList<Integer> evictingFPs = cache.remove(evictURL);
    	ArrayList<Integer> nonoverlappingFPs = new ArrayList<Integer>();
    	boolean overlapped=false;
    	for(int fp: evictingFPs){
    		for(String url: urlOrder){
    			if(cache.get(url).contains(fp)){
    				overlapped = true;
    			}
    		}
    		if(overlapped == false){
    			nonoverlappingFPs.add(fp);
    		}
    		overlapped = false;
    	}
    	return nonoverlappingFPs;
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
	public void processWebContent(String url, ArrayList<Integer> fps, IUCache urlcache) {
    	cache = urlcache;

    	if(!urlOrder.contains(url)){
			urlOrder.add(url);
		}
		
		misses = 0;
		hits = 0;
	        // For each chunk in the list, check to see if it's in the cache
			urlcache.put(url, fps);
			if(urlcache.isMiss()){
				misses++;
			}
			else{
				hits++;
			}
	    
		total_size = fps.size();
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
