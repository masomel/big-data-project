
 /*
 *@author Madhuvanthi Jayakumar
 */
public class SimpleCache{

/* Sample: Prototype deduplication with relatively small amounts of data
	small enough to be stored in main memory.
	Main purpose: Simulate caching and determine content overlap 
	within a website as well as between websites 
	MODEL:  |-------------|
			|FP1 | chunk1 |  
			|----|--------|
			|FP2 | chunk2 | 
			|----|--------|
			|FP3 | chunk3 | 
			|-------------|*/

	private HashTable cache;
	private double missrate;

	public Cache(){
		cache = new HashTable()<String,Chunk>;
		missrate=0;

	}

	public double getMissRate(){
		return hitrate;
	}

	public HashTable getCache(){
		return cache;
	}

	/** @params: url, chunked content of the webpage in byte array
	    @return: int array with content or fingerprint 
	    Procedure: 
	    At <Key = url> in the cache, iterate through 
	    array list to determine overlap
		If fingerprints match, use fingerprint
		Else Replace and use content.
		Sets hitrate equal to the percent of matches
	     */
	public byte[] getWebContent(Chunk[] content){
		hitrate=0;
		ArrayList diff = new ArrayList();
		while(int i= 0; i<len; i++){
			diff.add(content[i].fingerprint);	
			if(!cache.containsKey(content[i].fingerprint)){
				cache.put(i, content[i].fingerprint)
				missrate+=1;
			}
		}
		missrate = missrate/content.length;
		Chunk[] chunksOfWebpage = FPArrayListToChunkArray(diff);
		return reconstructData(chunksOfWebpage);
		//set size of cache, LRU? FIFO? LIFO?
	}

	/** COULD BE IN URL CLASS? */
	private Chunk[] FPArrayListToChunkArray(ArrayList fp){
		int len = fp.size();
		Chunk[] chunk = new Chunk[len];
		for(int i=0; i<len; i++){
			chunk[i] = fp.get(i);
		}

	}

	/** COULD BE IN URL CLASS? */
	/** Given array of Chunk objects, reconstruct the full web content 
		in the form of a byte array using the data from the chunks*/ 
	private byte[] reconstructData(Chunk[] chunk){
		int len = chunk.length*10;
		byte[] webcontent = new byte[len]; //ten hardcoded (size of each chunk) ?changelater
		byte[] chunkData = new byte[10]; //hardcoded, change later
		for(int i=0; i<len; i++){
			chunkData = chunk.getData(i);
			for(int j=0; j<10; j++){
				webcontent[i] = chunkData[j];
			}
		}
		return webcontent; //returns entirety of everything in chunks, constructing a full webpage..
	}

}