package devices;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import processing.IProcessor;
import processing.SimpleProcessor;

import caching.ICache;
import caching.MRUCache;
import chunking.Chunk;
import chunking.Chunking;

public class Mobile{
    
    private IProcessor proc;
    private ICache cache;
    private ArrayList<Integer> allFps;
    private ArrayList<Integer> neededFps;
    private ArrayList<Chunk> neededChunks;
    private byte[] webcontent;

    public Mobile(int size){
	cache = new MRUCache(size); // Mobile cache holds size chunks
	proc = new SimpleProcessor();
	allFps = new ArrayList<Integer>();
	neededFps = new ArrayList<Integer>();
	neededChunks = new ArrayList<Chunk>();
	webcontent = new byte[1]; //dummy array for now
    }

    public ArrayList<Integer> sendNeededFps(){
	neededFps = findNeededFps(allFps);
	return neededFps;
    }

    public void receiveFps(ArrayList<Integer> fps){
	allFps = fps;
    }

    public void receiveChunks(ArrayList<Chunk> chunks){
	neededChunks = chunks;
    }

    public ICache getCache(){
	return cache;
    }

    /** Given an array of fingerprints, find the redundant fingerprints, and
     * return a list of the fingerprints needed to reconstruct the web content.
     *@param fps the list of fingerprints to check.
     *@return the list of fingerprints not found in the cache.
     */
    private ArrayList<Integer> findNeededFps(ArrayList<Integer> fps){

	ArrayList<Integer> diff = new ArrayList<Integer>();

	for(Integer fp : fps){

	    if(cache.get(fp) == null){
		diff.add(fp);
	    }
	    else{
		diff.add(null); // needed for reconstruction purposes
	    }
	 
	}

	return diff;

    } //ends findNeededFps()

    /** Given an Arraylist of Chunk objects, and a list of all fingerprints, for the webpage
     *reconstruct the full web content in the form of a byte array using the data from the chunks
     */
    public byte[] reconstructData() throws FileNotFoundException{
	
	// the length of the entire webpage
	int len = neededChunks.size()*Chunking.getChunkSize(); 

	webcontent = new byte[len]; // array to hold the reconstructed web data

	byte[] chunkData = new byte[Chunking.getChunkSize()];

	int offset = 0; // offset in web content array

	for(int i = 0; i < neededChunks.size(); i++){

	    Chunk c = neededChunks.get(i);

	    if(c == null){
		int fp = allFps.get(i);
		Chunk cacheChunk = cache.get(fp);

		if(cacheChunk == null){
		    System.out.println("Something went wrong when mapping.");
		}
		
		chunkData = cacheChunk.getData();
	    }
	    else{
		chunkData = c.getData();
	    }
	    
	    for(int j = 0; j < Chunking.getChunkSize(); j++){
		webcontent[offset+j] = chunkData[j];
	    }

	    offset += Chunking.getChunkSize();

	}

	proc.processWebContent(neededChunks, cache);
	return webcontent;
    } //ends reconstructData()

    /** Given an Arraylist of Chunk objects, and a list of all fingerprints, for the webpage
     *reconstruct the full web content in the form of a byte array using the data from the chunks
     */
    public byte[] reconstructDataSliding() throws FileNotFoundException{
	
	// the length of the entire webpage
	int len = Chunking.getContentLength(); 

	System.out.println(len);

	webcontent = new byte[len]; // array to hold the reconstructed web data

	byte[] chunkData = new byte[1];

	int offset = 0; // offset in web content array

	System.out.println(neededChunks.size());

	for(int i = 0; i < neededChunks.size(); i++){

	    Chunk c = neededChunks.get(i);

	    if(c == null){
		int fp = allFps.get(i);
		Chunk cacheChunk = cache.get(fp);

		if(cacheChunk == null){
		    System.out.println("Something went wrong when mapping.");
		}
		
		chunkData = cacheChunk.getData();
	    }
	    else{
		chunkData = c.getData();
	    }

	    //System.out.println(chunkData.length);
	    
	    int j = 0;
	    try{
		for(j = 0; j < chunkData.length; j++){
		    webcontent[offset+j] = chunkData[j];
		}
	    }
	    catch(IndexOutOfBoundsException e){
		System.out.println(chunkData.length);
		System.out.println(offset);
		System.out.println(j);
	    }

	    offset += chunkData.length;
	    //System.out.println(i);

	}

	proc.processWebContent(neededChunks, cache);
	return webcontent;
    } //ends reconstructDataSliding()
    
    public IProcessor getProcessor() {
    	return proc;
    }

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
} //ends Mobile class

	/* FOR DEBUGGING!
	PrintWriter out = new PrintWriter("./amazon1-recon.txt");

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

	    out.println(output);
	}

	out.flush();
	out.close();
	*/
	// return webcontent; //returns entirety of everything in chunks, constructing a full webpage.
