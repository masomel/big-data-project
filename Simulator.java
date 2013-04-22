public class Simulator{

	public static void main(String args[]){
		Chunking web_data = Chunking.getInstance();
		Chunk chunk1 = web_data.getNextChunk();
		chunk1.toString();

		/* Fingerprinting should accept chunks, not just byte array*/
		/* For now, treating it as a constructor */
		Fingerprinting fp = new Fingerprinting(chunk1.getData());
	}

}