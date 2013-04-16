package chunking;

import java.io.BufferedReader;

public class Chunking {

	private static final Chunking instance = new Chunking();
	private BufferedReader stream;
	private int chunk_size;

	public static Chunking getInstance() {
		return instance;
	}

	public Chunk getNextChunk() {
		Chunk result;
				
		return result;
	}

	private Chunking() {	
	}
}
