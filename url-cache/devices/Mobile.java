package devices;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

public class Mobile{
    
	private IProcessor proc;
	private IUProcessor urlProc;
    private ICache cache;
    private IUCache urlcache;
    private byte[] webcontent;

    public Mobile(int size){
		cache = new MRUCache(size); // Mobile cache holds size chunks
		urlcache = new URLCache(size);
		proc = new SimpleProcessor();
		urlProc = new URLProcessor();
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
    	return urlProc;
    }
    
    /** 
     * @param url
     * @return true if url is in the cache and false otherwise
     */
	public ArrayList<Integer> processHeader(String url){
		if(urlcache.get(url) == null)
			return null;
		else
			return urlcache.get(url);
	}
    
	/**
	 * Update caches
	 * @param chunks
	 * @param fps
	 */
    public void processChunks(ArrayList<Chunk> chunks, ArrayList<Integer> fps, String url){
    	urlProc.processWebContent(url, fps, urlcache);
		proc.processWebContent(fps, chunks, cache);		
	}

    /** Given an Arraylist of Chunk objects, and a list of all fingerprints, for the webpage
     *reconstruct the full web content in the form of a byte array using the data from the chunks
     */
    public byte[] reconstructData(String url, ArrayList<Chunk> diff, ArrayList<Integer> proxyFPs) throws FileNotFoundException{
		ArrayList<Integer> reconstructFPs = new ArrayList<Integer>();
		int chunkSize = Chunking.getChunkSize();
		byte[] chunkData = new byte[chunkSize];
		int offset = 0;
		reconstructFPs = proxyFPs;
		//reconstructFPs = urlcache.get(url);
		//int len = reconstructFPs.size()*Chunking.getChunkSize(); //length of entire page
		int len = proxyFPs.size()*Chunking.getChunkSize(); //length of entire page
		webcontent = new byte[len];
		for(int i=0; i<reconstructFPs.size(); i++){
			int fp = reconstructFPs.get(i);
			if(cache.get(fp)!= null)
				chunkData = cache.get(fp).getData();
			else
				chunkData = diff.get(i).getData();
			for(int j = 0; j < chunkSize; j++){
				webcontent[offset+j] = chunkData[j];
			}
			offset = offset+chunkSize;
		}
		return webcontent;
    } //ends reconstructData()
    

    
    public void outputData(byte[] webcontent, String path, String filename, int webNum){
    	PrintWriter out;
		try {
			File dir = new File(path + "/ReconstructedBytes/");
			dir.mkdir();
			out = new PrintWriter(path + "/ReconstructedBytes/" + filename + webNum + "-recon.txt");
    	int len = webcontent.length;
    	for(int i = 0; i < len; i++){
    	    String hex = Integer.toHexString((int)webcontent[i]);
    	    String output = "";

    	    if(hex.length() > 2){
    		output = hex.substring(hex.length()-2, hex.length());
    	    }
    	    else if(hex.length() < 2){
    		output = "0"+hex;
    	    }
    	    else{
    		output = hex;
    	    }

    	    out.println("0x" + output);
    	}

    	out.flush();
    	out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
} 