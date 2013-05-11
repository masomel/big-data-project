package devices;
import java.io.IOException;
import java.util.ArrayList;

import processing.IProcessor;
import processing.IUProcessor;
import processing.SimpleProcessor;
import processing.URLProcessor;

import caching.ICache;
import caching.IUCache;
import caching.MRUCache;
import caching.URLCache;
import chunking.Chunk;
import chunking.Chunking;
import fingerprinting.Fingerprinting;

public class ProxyServer{
    
	private IProcessor proc;
	private IUProcessor urlproc;
	private ICache cache;
    private IUCache urlcache;
    private ArrayList<Integer> allFps;
    private byte[] webcontent;

    public ProxyServer(int size){
		cache = new MRUCache(size); // Server cache holds size chunks
		urlcache = new URLCache(size);
		proc = new SimpleProcessor();
		urlproc = new URLProcessor();
		allFps = new ArrayList<Integer>();
    }
    
    public ICache getCache(){
		return cache;
    }
    
    public IUCache getURLCache(){
    	return urlcache;
    }

    public IProcessor getProcessor() {
    	return proc;
    }

    public IUProcessor getURLProcessor() {
    	return urlproc;
    } 
    
    
    /**
     * Check to see if it is in the cache.
     * @param url
     * @param mobileFPs
     * @return
     */
    public ArrayList<Integer> processRequest(String url, ArrayList<Integer> mobileFPs){
		if(urlcache.get(url) == null)
			return null;
		else{
			return urlcache.get(url);
		}
    }
    
    
    public ArrayList<Chunk> chunkWebData(String path, int chunkSize){
    	ArrayList<Chunk> chunks = new ArrayList<Chunk>();
    	Chunking.setFile(path);
    	Chunking.setChunkSize(chunkSize);
	    // read next chunks while the end of the file has not been reached
	    while(!Chunking.isEOF()) {
	    	try {
	    		Chunk c = Chunking.getNextChunk();

                // ensure that we don't add empty chunk to our list
                if(Chunking.isEOF()){
                    break;
                }
                chunks.add(c);
	    	} catch (IOException ioe) {
	    		ioe.printStackTrace();
	    	}
	    }
	    return chunks;
    }
    
    public ArrayList<Integer> addFPsToCaches(String url, ArrayList<Chunk> chunks){
    	
    	ArrayList<Integer> proxyFPs = getFingerprints(chunks);
    	
    	urlproc.processWebContent(url, proxyFPs, urlcache);
    	proc.processWebContent(proxyFPs, chunks, cache);
    	return proxyFPs;
    }
    
    public ArrayList<Chunk> calculateDiff(ArrayList<Integer> mobileFPs, ArrayList<Integer> proxyFPs){
    	ArrayList<Chunk> diff = new ArrayList<Chunk>();
    	if(mobileFPs == null){
    		for(int fp: proxyFPs){
        		diff.add(cache.get(fp));
        	}
    	}
    	
    	else{
	    	for(int fp: proxyFPs){
	    		if(mobileFPs.contains(fp)){
	    			diff.add(null);
	    		}
	    		else{
	    			diff.add(cache.get(fp));
	    		}
	    	}
    	}
    	return diff;
    }

    /** Given a list of chunks, get the fingerprint of each chunk. This will be used to send
     * the fingerprints to the mobile cache, so the mobile device can check its cache for the
     * pages.
     */
    private ArrayList<Integer> getFingerprints(ArrayList<Chunk> content){
		ArrayList<Integer> fps = new ArrayList<Integer>();
	
		for(Chunk chunk : content){
		    int fp = Fingerprinting.fingerprint(chunk.getData());
		    fps.add(fp);
		}
	
		return fps;
    }


}