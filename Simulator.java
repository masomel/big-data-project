import java.io.IOException;
import java.util.ArrayList;

/** Simulates the interactions between a proxy server, and a mobile device */
public class Simulator{

    private static Chunking webData = new Chunking("./packet_bytes/amazon1.txt");

    private static SimpleCache proxyCache = new SimpleCache();

    private static SimpleCache mobCache = new SimpleCache(1024); // Mobile cache holds 10KB or 1024 chunks

    public static void main(String args[]) throws IOException{
	
	ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	
	System.out.println("Proxy server grabs all the web data for a requested webpage");

	System.out.println("Proxy server partitions the received data into fixed-size chunks");

	// read next chunks while the end of the file has not been reached
	while(!webData.isEOF()){		
	    Chunk c = webData.getNextChunk();
	    
	    // ensure that we don't add empty chunk to our list
	    if(webData.isEOF()){
		break;
	    }
	    
	    chunks.add(c);
	}
	
	System.out.println("Proxy server is computing the fingerprints for all received chunks.");

	// Proxy computes the fingerprints of all the chunks it received
	ArrayList<Integer> fps = getFingerprints(chunks);

	System.out.println("Proxy server is sending the computed fingerprints to the mobile device.");

	System.out.println("Mobile device is checking its cache for the fingerprints it received from the proxy server.");

	// Mobile device checks its cache for the fps it received from the proxy
	ArrayList<Integer> mobRed = mobCache.getRedundantFps(fps);

	System.out.println("Mobile device sends back a list of fingerprints of the chunks it does not have in its cache.");
	
	System.out.println("Number of chunks inspected: "+chunks.size());
	System.out.println("Number of redundant chunks: "+redundant.size());
	System.out.println("Remaining cache capacity: "+cache.getCapacity());

	/* Chunk[] chunksOfWebpage = FPArrayListToChunkArray(FPs, cache);
	   byte[] entirePage = reconstructData(chunksOfWebpage);*/
	System.out.println("Missrate: " + cache.getMissRate());
	
    }
    
    
    /*
      private static Chunk[] FPArrayListToChunkArray(ArrayList fp, SimpleCache cache){
      int len = fp.size();
      Chunk[] chunks = new Chunk[len];
      for(int i=0; i<len; i++){
      chunks[i] = (Chunk)(cache.get((Integer)(fp.get(i))));
      }
      return chunks;
      
      }
      /** Given array of Chunk objects, reconstruct the full web content 
      in the form of a byte array using the data from the chunks
      private static byte[] reconstructData(Chunk[] chunk){
      int len = chunk.length*10;
      byte[] webcontent = new byte[len]; //ten hardcoded (size of each chunk) ?changelater
      byte[] chunkData = new byte[10]; //hardcoded, change later
      for(int i=0; i<len; i++){
      chunkData = chunk[i].getData();
      for(int j=0; j<10; j++){
      webcontent[i] = chunkData[j];
      }
      }
      return webcontent; //returns entirety of everything in chunks, constructing a full webpage..
      }*/

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
    
}