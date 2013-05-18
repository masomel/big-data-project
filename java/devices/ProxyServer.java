package devices;
import java.io.IOException;
import java.util.ArrayList;

import processing.IProcessor;
import processing.SimpleProcessor;

import caching.ICache;
import caching.MRUCache;
import chunking.Chunk;
import chunking.Chunking;
import fingerprinting.Fingerprinting;

public class ProxyServer{
    
    private IProcessor proc;
    private ICache cache;
    private ArrayList<Chunk> allChunks;
    private ArrayList<Integer> allFps;
    private ArrayList<Integer> neededFps;
    private byte[] webcontent;

    public ProxyServer(int size){
	cache = new MRUCache(size); // Server cache holds size chunks
	proc = new SimpleProcessor();
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

    public ICache getCache(){
	return cache;
    }

    public IProcessor getProcessor() {
    	return proc;
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

    public ArrayList<Chunk> chunkWebDataSliding(String path, int windowSize) throws IOException{
	Chunking.setFile(path);
	Chunking.setWindowSize(windowSize);
    	ArrayList<Chunk> chunks = Chunking.getAllChunksSliding();
	return chunks;
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
	proc.processWebContent(allChunks, cache);
	
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
		if(cache.get(curFp) != null){
		    prepData.add((Chunk)cache.get(curFp));
		}
		else if(cache.get(curFp) == null){
		    prepData.add((Chunk)allChunks.get(i));
		}
	    }	    
	    
	}

	return prepData;
	
    } //ends prepareData()

}