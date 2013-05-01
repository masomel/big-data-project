import java.util.*;

public class Mobile{
    
    private SimpleCache mobCache;
    private ArrayList<Integer> allFps;
    private ArrayList<Integer> neededFps;
    private ArrayList<Chunk> neededChunks;
    private byte[] webcontent;

    public Mobile(){
	mobCache = new SimpleCache(1024); // Mobile cache holds 10KB or 1024 chunks
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

    /** Given an array of fingerprints, find the redundant fingerprints, and
     * return a list of the fingerprints needed to reconstruct the web content.
     *@param fps the list of fingerprints to check.
     *@return the list of fingerprints not found in the cache.
     */
    private ArrayList<Integer> findNeededFps(ArrayList<Integer> fps){
	int misses = 0;

	ArrayList<Integer> diff = new ArrayList<Integer>();

	for(Integer fp : fps){

	    if(!cache.containsKey(fp)){
		diff.add(fp);
		misses++;
	    }
	    else{
		diff.add(null); // needed for reconstruction purposes
	    }
	 
	}
	missrate = misses/content.size();
	return diff;

    } //ends findNeededFps()

    /** Given an Arraylist of Chunk objects, and a list of all fingerprints, for the webpage
     *reconstruct the full web content in the form of a byte array using the data from the chunks
     */
    public void reconstructData(){
	
	// the length of the entire webpage
	int len = neededChunks.size()*10; //hardcoded for now

	webcontent = new byte[len]; // array to hold the reconstructed web data

	byte[] chunkData = new byte[10]; //hardcoded for now

	int offset = 0; // offset in web content array

	for(int i = 0; i < neededChunks.size(); i++){

	    Chunk c = neededChunks.get(i);

	    if(c == null){
		chunkData = cache.get(allFps.get(i)).getData();
	    }
	    else{
		chunkData = c.getData();
	    }
	    
	    for(int j = 0; j < 10; j++){
		webcontent[offset+j] = chunkData[j];
	    }

	    offset += 10;

	}

	PrintWriter out = new PrintWriter("./amazon1-recon.txt");

	for(int i = 0; i < len; i++){
	    out.println(Integer.toHexString((int)webcontent[i]));
	}

	out.flush();
	out.close();

	return webcontent; //returns entirety of everything in chunks, constructing a full webpage.

    } //ends reconstructData()

} //ends Mobile class