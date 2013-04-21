package chunking;

import java.io.FileInputStream;
import java.io.IOException;

public class Chunking {

	private static final Chunking instance = new Chunking();
	private FileInputStream stream;
	private int chunk_size;

	public static Chunking getInstance() {
		return instance;
	}

	public Chunk getNextChunk() throws IOException {
		
		if (stream.available() > 0) {
			byte[] data = new byte[chunk_size];

			int f = stream.read(data, 0, chunk_size);
			if (f == chunk_size)
				return new Chunk(chunk_size, data);
			else {
				// fill the byte array with zeroes up to the end
				for (int i = f; i < data.length; i++) {
					data[i] = 0;
				}
				stream.close();
				return new Chunk(chunk_size, data);
			}
		}
		else return new Chunk(chunk_size);
	}

	private Chunking() {
		// specify the chunk size here
		chunk_size = 10;
		// specify the input file here
		String file_name = "file.txt";
		try {
			stream = new FileInputStream(file_name);
		}
		catch (IOException e) {
			System.err.println("Cannot open file " + file_name);
			e.printStackTrace();
		}
	}
}
