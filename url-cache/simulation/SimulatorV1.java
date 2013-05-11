package simulation;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import caching.ICache;
import chunking.Chunk;
import chunking.Chunking;
import devices.Mobile;
import devices.ProxyServer;

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

		// A proxy server
		ProxyServer proxy = new ProxyServer(proxyCacheSize); //holds 2000 chunks; TODO: this should be an argument

		// A mobile device; Could imagine simulating multiple devices
		Mobile mobile = new Mobile(mobileCacheSize); //holds 500 chunks; TODO: this should be an argument

		// Used to calculated the average missrates
		double proxySum = 0;
		double mobSum = 0;
		String url;
		ArrayList<Integer> mobileFPs= new ArrayList<Integer>();
		ArrayList<Integer> proxyFPs = new ArrayList<Integer>();
		ArrayList<Chunk> diff = new ArrayList<Chunk>();

		// loop over all files of a given website and look at stats
		for(int i = 1; i <= numIters; i++) { // Caution: Hardcoded!
			
			url=website + i; //extract header information from file.
			
			mobileFPs = mobile.processHeader(url); 
			//URL NOT IN MOBILE CACHE
			
			//if(mobileFPs == null){	
				proxyFPs = proxy.processRequest(url, mobileFPs);
				//URL NOT IN PROXY CACHE
				if(proxyFPs == null){
					//get data back and chunk
					ArrayList<Chunk> chunks = new ArrayList<Chunk>();
					chunks = proxy.chunkWebData(path +"/packet_bytes/" + website + i + ".txt", chunk_size);
					System.out.println("Processing file: " + Chunking.getFilename());
					proxyFPs = proxy.addFPsToCaches(url, chunks); //calculate FPs and add
					
				}
				//SEND DIFF as RESPONSE TO MOBILE
				diff = proxy.calculateDiff(mobileFPs, proxyFPs);
				
				
				//MOBILE UPDATES CACHES
			    mobile.processChunks(diff, proxyFPs, url);
			//}
			    
			    // System.out.println("Mobile device reconstructs the web data of the requested webpage.");
			    try {
			    	byte[] webdata = mobile.reconstructData(url, diff, proxyFPs);
			    	mobile.outputData(webdata, path, website, i);
			    } catch (FileNotFoundException fnfe) {
			    	fnfe.printStackTrace();
			    }
			    
			    
			    System.out.println("----------------------");
			    
			   // System.out.println("Number of chunks inspected: "+chunks.size());	
			    System.out.println("Remaining proxy cache capacity: "+proxy.getCache().getCapacity());
			    System.out.print("Proxy missrate: ");
			    customFormat("##.####", proxy.getProcessor().getMissRate());
			    System.out.println("Remaining mobile cache capacity: "+mobile.getCache().getCapacity());
			    System.out.print("Mobile missrate: ");
			    customFormat("##.####", mobile.getProcessor().getMissRate());
			    System.out.println();
			    
			    System.out.println("Remaining proxy url cache capacity: " + proxy.getURLCache().getCapacity());
			    System.out.println("Remaining mobile url cache capacity: " + mobile.getURLCache().getCapacity());
			    proxySum += proxy.getProcessor().getMissRate();
			    mobSum += mobile.getProcessor().getMissRate();		
				// Hardcoded number of sites!
				System.out.print("Avg proxy missrate over all inspected sites: ");
				customFormat("##.####", proxySum/numIters);
				System.out.print("Avg mobile missrate over all inspected sites: ");
				customFormat("##.####", mobSum/numIters);
		} 
	}
        
    private static Chunk[] FPArrayListToChunkArray(ArrayList<Integer> fp, ICache cache){
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
