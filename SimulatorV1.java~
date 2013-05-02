import java.io.IOException;
import java.util.ArrayList;
import java.text.*;

/** Simulates the interactions between a proxy server, and a mobile device */
public class Simulator{

    public static void main(String args[]) throws IOException{

	// Enter chunk size as argument when running the Simulator for stats purposes!
	int chunk_size = 0;

	if(args.length != 1){
	    System.out.println("Format: java Simulator [postive int: chunk_size]");
	    System.exit(-1);
	}

	try{
	    chunk_size = Integer.parseInt(args[0]);
	}
	catch(NumberFormatException e){
	    System.out.println("Chunk size must be positive integer!!!!");
	    System.exit(-1);
	}
	
	// The Chunking Facility
	Chunking chunking = new Chunking("./packet_bytes/amazon1.txt", chunk_size);

	// TODO: move to Proxy class
	SimpleCache proxyCache = new SimpleCache();

	// A mobile device; Could imagine simulating multiple devices
	Mobile mobile = new Mobile();

	// Used to calculated the average missrates
	double proxySum = 0;
	double mobSum = 0; 

	// loop over all files of a given website and look at stats
	for(int i = 1; i <= 8; i++){ // Caution: Hardcoded!
	    chunking.reset("./packet_bytes/amazon"+i+".txt");

	    System.out.println("Processing file: "+chunking.getFilename());
	
	    ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	    
	    /*
	    System.out.println("Proxy server grabs all the web data for a requested webpage");
	    
	    System.out.println("Proxy server partitions the received data into fixed-size chunks");
	    */

	    // read next chunks while the end of the file has not been reached
	    while(!chunking.isEOF()){		
		Chunk c = chunking.getNextChunk();
		
		// ensure that we don't add empty chunk to our list
		if(chunking.isEOF()){
		    break;
		}
		
		chunks.add(c);
	    }
	    
	    /*
	    System.out.println("Proxy server is computing the fingerprints for all received chunks.");
	    */

	    // Proxy computes the fingerprints of all the chunks it received
	    ArrayList<Integer> fps = getFingerprints(chunks);
	    
	    /*
	    System.out.println("Proxy server is sending the computed fingerprints to the mobile device.");
	    */

	    mobile.receiveFps(fps);

	    /*
	    System.out.println("Mobile device is checking its cache for the fingerprints it received from the proxy server.");
	    */

	    proxyCache.processWebContent(chunks);
	    
	    // Mobile device checks its cache for the fps it received from the proxy
	    ArrayList<Integer> mobNeeded = mobile.sendNeededFps();
	    
	    /*
	    System.out.println("Mobile device sends back a list of fingerprints of the chunks it does not have in its cache.");
	    */

	    ArrayList<Chunk> neededChunks = proxyCache.prepareData(chunks, mobNeeded);
	    
	    /*
	    System.out.println("Proxy sends back a list of chunks according to the needed fingerprints.");
	    
	    System.out.println("Mobile device caches the received content.");
	    */

	    mobile.receiveChunks(neededChunks);
	    
	    /*
	    System.out.println("Mobile device reconstructs the web data of the requested webpage.");
	    */

	    mobile.reconstructData();
	    
	    System.out.println("----------------------");
	    
	    System.out.println("Number of chunks inspected: "+chunks.size());	
	    System.out.println("Remaining proxy cache capacity: "+proxyCache.getCapacity());
	    System.out.print("Proxy missrate: ");
	    customFormat("##.####", proxyCache.getMissRate());
	    System.out.println("Remaining mobile cache capacity: "+mobile.getMobCache().getCapacity());
	    System.out.print("Mobile missrate: ");
	    customFormat("##.####", mobile.getMobCache().getMissRate());
	    System.out.println();

	    proxySum += proxyCache.getMissRate();
	    mobSum += mobile.getMobCache().getMissRate();
	    
	}

	// Hardcoded number of sites!
	System.out.print("Avg proxy missrate over all inspected sites: ");
	customFormat("##.####", proxySum/8);
	System.out.print("Avg mobile missrate over all inspected sites: ");
	customFormat("##.####", mobSum/8);
    }
        
    private static Chunk[] FPArrayListToChunkArray(ArrayList<Integer> fp, SimpleCache cache){
	int len = fp.size();
	Chunk[] chunks = new Chunk[len];
	for(int i=0; i<len; i++){
	    chunks[i] = (Chunk)(cache.get((Integer)(fp.get(i))));
	}
	return chunks;
	
    }

    // TODO: move to Proxy class
     /** Given a list of chunks, get the fingerprint of each chunk. This will be used to send
     * the fingerprints to the mobile cache, so the mobile device can check its cache for the
     * pages.
     */
    private static ArrayList<Integer> getFingerprints(ArrayList<Chunk> content){
	ArrayList<Integer> fps = new ArrayList<Integer>();

	for(Chunk chunk : content){
	    int fp = Fingerprinting.fingerprint(chunk.getData());
	    fps.add(fp);
	}

	return fps;
    }

    /** Helper function:
     * Formats how the doubles are printed given a specific pattern
     */
    static public void customFormat(String pattern, double value ) {
	DecimalFormat myFormatter = new DecimalFormat(pattern);
	String output = myFormatter.format(value);
	System.out.println(output);
    }
    
}