package chunking;
import java.io.InputStream;
import java.io.IOException;
import java.util.Scanner;

public class ChunkingNet {

    /* private static final Chunking instance = new Chunking(); */
    private static InputStream stream;
    private static Scanner in; // use Scanner for now since input files are .txt format
    private static int chunk_size;
    private static String filename;
    private static boolean EOF; // flag to mark when the end of a file has been reached

    public static Chunking getInstance() {
	return null;
    }

    public static String getFilename(){
	return filename;
    }

    public static int getChunkSize(){
	return chunk_size;
    }

    public static boolean isEOF(){
	return EOF;
    }
    
    public static void setChunkSize(int size){
    	chunk_size=size;
    }

    /** Set the scanner to chunk to the current
     * one in the main ProxyServerNet class
     * @param sc the scanner to use to chunk the web data
     */
    public static void setStream(InputStream i){
	EOF = false;
	stream = i;
    }
    
    /**  param: Needed since we don't want to hardcode in specific
     * filenames in here!
    public static void setFile(String f) {
	// specify the chunk size here
	EOF = false;
	// specify the input file here
	filename = f;
	try {
	    stream = new FileInputStream(filename);
	    in = new Scanner(stream);
	}
	catch (IOException e) {
	    System.err.println("Cannot open file " + filename);
	    e.printStackTrace();
	}
	}*/
    
    public static Chunk getNextChunk() throws IOException {

	// if the stream has at least one more byte, process
	if(stream.available() > 0){
	    EOF = false;
	   
	    byte[] data = new byte[chunk_size];
	    
	    // count how many bytes the stream reads in
	    int f = stream.read(data, 0, chunk_size);

	    if (f == chunk_size){
		return new Chunk(chunk_size, data);
	    }
	    else {
		// fill the byte array with zeroes up to the end
		for (int i = f; i < data.length; i++) {
		    data[i] = 0;
		}
		return new Chunk(chunk_size, data);
	    }
	}
	else {
	    EOF = true;
	    return new Chunk(chunk_size);
	}
       
    }    

}
