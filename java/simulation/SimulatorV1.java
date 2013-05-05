package simulation;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import devices.ProxyServer;
import devices.Mobile;
import caching.SimpleCache;
import chunking.Chunk;
import chunking.Chunking;

/**
 * Class representing the first version of the simulator.
 * @author Marcela Melara, Nayden Nedev
 *
 */
public class SimulatorV1 implements ISimulator {

	private String path;
	private String website;
	private int chunk_size;
	private int proxyCacheSize;
	private int mobileCacheSize;
	private int numIters;

	public SimulatorV1(String path, String website, int chunk_size, int proxyCacheSize, int mobileCacheSize, int numIters) {
		this.path = path;
		this.website = website;
		this.chunk_size = chunk_size;
		this.proxyCacheSize = proxyCacheSize;
		this.mobileCacheSize = mobileCacheSize;
		this.numIters = numIters;
	}

	@Override
	public void simulate() {
		// The Chunking Facility
		Chunking chunking = new Chunking(path+"/packet_bytes/"+website+"1.txt", chunk_size); //assume that we always want to start analyzing at 1st page

		// A proxy server
		ProxyServer proxy = new ProxyServer(proxyCacheSize); //holds 2000 chunks; TODO: this should be an argument

		// A mobile device; Could imagine simulating multiple devices
		Mobile mobile = new Mobile(mobileCacheSize); //holds 500 chunks; TODO: this should be an argument

		// Used to calculated the average missrates
		double proxySum = 0;
		double mobSum = 0;

		// loop over all files of a given website and look at stats
		for(int i = 1; i <= numIters; i++) { // Caution: Hardcoded!
		    chunking.reset(path +"/packet_bytes/" + website + i + ".txt");

		    System.out.println("Processing file: " + chunking.getFilename());

		    ArrayList<Chunk> chunks = new ArrayList<Chunk>();

		    // System.out.println("Proxy server grabs all the web data for a requested webpage");		    
		    // System.out.println("Proxy server partitions the received data into fixed-size chunks");

		    // read next chunks while the end of the file has not been reached
		    while(!chunking.isEOF()) {
		    	try {
		    		Chunk c = chunking.getNextChunk();

                    // ensure that we don't add empty chunk to our list
                    if(chunking.isEOF()){
                        break;
                    }
                    chunks.add(c);
		    	} catch (IOException ioe) {
		    		ioe.printStackTrace();
		    	}
		    }
		    
		    // System.out.println("Proxy server is computing the fingerprints for all received chunks.");

		    // Proxy computes the fingerprints of all the chunks it received
		    ArrayList<Integer> fps = proxy.sendAllFps(chunks);
		    
		    // System.out.println("Proxy server is sending the computed fingerprints to the mobile device.");

		    mobile.receiveFps(fps);

		    // System.out.println("Mobile device is checking its cache for the fingerprints it received from the proxy server.");
    
		    // Mobile device checks its cache for the fps it received from the proxy
		    ArrayList<Integer> mobNeeded = mobile.sendNeededFps();
		    
		    // System.out.println("Mobile device sends back a list of fingerprints of the chunks it does not have in its cache.");

		    proxy.receiveNeededFps(mobNeeded);

		    ArrayList<Chunk> neededChunks = proxy.sendNeededChunks();
		    
		    // System.out.println("Proxy sends back a list of chunks according to the needed fingerprints.");		    
		    // System.out.println("Mobile device caches the received content.");

		    mobile.receiveChunks(neededChunks);
		    
		    // System.out.println("Mobile device reconstructs the web data of the requested webpage.");
		    try {
		    	mobile.reconstructData();
		    } catch (FileNotFoundException fnfe) {
		    	fnfe.printStackTrace();
		    }
		    
		    System.out.println("----------------------");
		    
		    System.out.println("Number of chunks inspected: "+chunks.size());	
		    System.out.println("Remaining proxy cache capacity: "+proxy.getCache().getCapacity());
		    System.out.print("Proxy missrate: ");
		    customFormat("##.####", proxy.getCache().getMissRate());
		    System.out.println("Remaining mobile cache capacity: "+mobile.getCache().getCapacity());
		    System.out.print("Mobile missrate: ");
		    customFormat("##.####", mobile.getCache().getMissRate());
		    System.out.println();

		    proxySum += proxy.getCache().getMissRate();
		    mobSum += mobile.getCache().getMissRate();		
			// Hardcoded number of sites!
			System.out.print("Avg proxy missrate over all inspected sites: ");
			customFormat("##.####", proxySum/numIters);
			System.out.print("Avg mobile missrate over all inspected sites: ");
			customFormat("##.####", mobSum/numIters);
		} 
	}
        
    private static Chunk[] FPArrayListToChunkArray(ArrayList<Integer> fp, SimpleCache cache){
	int len = fp.size();
	Chunk[] chunks = new Chunk[len];
	for(int i=0; i<len; i++){
	    chunks[i] = (Chunk)(cache.get((Integer)(fp.get(i))));
	}
	return chunks;
	
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
