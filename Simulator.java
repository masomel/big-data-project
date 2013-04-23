import java.io.IOException;
import java.util.ArrayList;

public class Simulator{

	public static void main(String args[]) throws IOException{
		Chunking web_data = Chunking.getInstance();
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		int i = 0;
		while(i<50){ /* ? change this: while data in chunk is not all 0s*/
			chunks.set(i,web_data.getNextChunk());
			i++;
		}
		SimpleCache cache = new SimpleCache();
		ArrayList FPs = cache.getWebContent(chunks); 
		Chunk[] chunksOfWebpage = FPArrayListToChunkArray(FPs, cache);
		byte[] entirePage = reconstructData(chunksOfWebpage);
		System.out.println("Missrate: " + cache.getMissRate());
	}
	
	
	
	private static Chunk[] FPArrayListToChunkArray(ArrayList fp, SimpleCache cache){
		int len = fp.size();
		Chunk[] chunks = new Chunk[len];
		for(int i=0; i<len; i++){
			chunks[i] = (Chunk)(cache.get((Integer)(fp.get(i))));
		}
		return chunks;

	}
	/** Given array of Chunk objects, reconstruct the full web content 
		in the form of a byte array using the data from the chunks*/ 
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
	}

}