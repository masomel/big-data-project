package caching;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import chunking.Chunk;
import chunking.Chunking;

public class Mobile{
    
    private SimpleCache cache;
    private ArrayList<Integer> allFps;
    private ArrayList<Integer> neededFps;
    private ArrayList<Chunk> neededChunks;
    private byte[] webcontent;

    public Mobile(int size){
	cache = new SimpleCache(size); // Mobile cache holds size chunks
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

    public SimpleCache getCache(){
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

	    if(!cache.getCache().containsKey(fp)){
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
    public void reconstructData() throws FileNotFoundException{
	
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

	cache.processWebContent(neededChunks);


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

    } //ends reconstructData()

} //ends Mobile class
