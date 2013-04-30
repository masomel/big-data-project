import java.io.IOException;
import java.util.ArrayList;

public class Simulator{

    private static Chunking webData = new Chunking("./packet_bytes/amazon1.txt");

    private static SimpleCache cache = new SimpleCache();

    public static void main(String args[]) throws IOException{
	
	ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	
	// read next chunks while the end of the file has not been reached
	while(!webData.isEOF()){		
	    Chunk c = webData.getNextChunk();
	    
	    // ensure that we don't add empty chunk to our list
	    if(webData.isEOF()){
		break;
	    }
	    
	    chunks.add(c);
	}
	
	ArrayList<Integer> redundant = cache.getWebContent(chunks); 
	
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
    
}