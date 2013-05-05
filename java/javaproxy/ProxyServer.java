package javaproxy;
import java.io.*;
import java.util.ArrayList;
import caching.SimpleCache;
import chunking.Chunk;
import fingerprinting.Fingerprinting;

public class ProxyServer{
    
    private SimpleCache cache;
    private ArrayList<Chunk> allChunks;
    private ArrayList<Integer> allFps;
    private ArrayList<Integer> neededFps;
    private byte[] webcontent;

    public ProxyServer(int size){
	cache = new SimpleCache(size); // Server cache holds size chunks
	allChunks = new ArrayList<Chunk>();
	allFps = new ArrayList<Integer>();
	neededFps = new ArrayList<Integer>();
    }

    public void receiveNeededFps(ArrayList<Integer> needed){
	neededFps = needed;
    }

    public ArrayList<Integer> sendAllFps(ArrayList<Chunk> content){
	allChunks = content;
	allFps = getFingerprints(content);
	return allFps;
    }

    public ArrayList<Chunk> sendNeededChunks(){
	return prepareData(neededFps);
    }

    public SimpleCache getCache(){
	return cache;
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

    /** Given the list of all web data chunks and the list of the needed fingerprints,
     * create a list of chunks to be sent over to the mobile device. A null entry indicates that
     * the mobile device already has this chunk in its cache.
     */
    private ArrayList<Chunk> prepareData(ArrayList<Integer> neededFps){
	
	// Update cache before preparing the data for the mobile device
	cache.processWebContent(allChunks);
	
	ArrayList<Chunk> prepData = new ArrayList<Chunk>();
	
	if(allChunks.size() != neededFps.size()){
	    System.out.println("Content and neededFps are not of the same length!");
	    return null;
	}
	
	for(int i = 0; i < allChunks.size(); i++){
	    
	    // check first to see if mobile device needs this chunk
	    if(neededFps.get(i) == null){
		prepData.add(null);
	    }
	    else{
		int curFp = neededFps.get(i);
		// check to see if we already have this chunk in our cache and if the mobile device needs it
		if(cache.getCache().containsKey(curFp)){
		    prepData.add((Chunk)cache.get(curFp));
		}
		else if(!cache.getCache().containsKey(curFp)){
		    prepData.add((Chunk)allChunks.get(i));
		}
	    }	    
	    
	}

	return prepData;
	
    } //ends prepareData()

}