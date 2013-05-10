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

public class MobileNet{
 
    private IProcessor proc;
    private ICache cache;
    private ArrayList<Integer> allFps;
    private ArrayList<Integer> neededFps;
    private ArrayList<Chunk> neededChunks;
    private byte[] webcontent;
    private int chunkSize;

    public MobileNet(int size, int cs){
	cache = new MRUCache(size); // Mobile cache holds size chunks
	proc = new SimpleProcessor();
	allFps = new ArrayList<Integer>();
	neededFps = new ArrayList<Integer>();
	neededChunks = new ArrayList<Chunk>();
	webcontent = new byte[1]; //dummy array for now
	chunkSize = cs;
    }

    public ICache getCache(){
	return cache;
    }

    public IProcessor getProcessor() {
    	return proc;
    }
    
    public ArrayList<Integer> getNeededFps(){
	neededFps = findNeededFps(allFps);
	return neededFps;
    }
    
    public void receiveAllFps(int fp){
	allFps.add(fp);
    }

    public void receiveNeededChunks(Chunk c){	
	neededChunks.add(c);
    }

    public void resetLists(){
	allFps = new ArrayList<Integer>();
	neededFps = new ArrayList<Integer>();
	neededChunks = new ArrayList<Chunk>();
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
    public void reconstructData() throws FileNotFoundException{
	
	// the length of the entire webpage
	int len = neededChunks.size()*chunkSize; 

	webcontent = new byte[len]; // array to hold the reconstructed web data

	byte[] chunkData = new byte[chunkSize];

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
	    
	    for(int j = 0; j < chunkSize; j++){
		webcontent[offset+j] = chunkData[j];
	    }

	    offset += chunkSize;

	}

	proc.processWebContent(neededChunks, cache);

    } //ends reconstructData()

    public void outputData(String path, String filename){
    	PrintWriter out;
	try {
	    File dir = new File(path + "/ReconstructedData/");
	    dir.mkdir();
	    out = new PrintWriter(path + "/ReconstructedData/" + filename + "-recon.html");
	    
	    String content = new String(webcontent);

	    content = content.substring(content.indexOf('<'), content.length());

	    out.println(content.trim());
	   
	    out.flush();
	    out.close();
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
} //ends Mobile class