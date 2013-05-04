package chunking;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Chunking {

    /* private static final Chunking instance = new Chunking(); */
    private static FileInputStream stream;
    private static Scanner in; // use Scanner for now since input files are .txt format
    private static int chunk_size;
    private static String filename;
    private static boolean EOF; // flag to mark when the end of a file has been reached

    public static Chunking getInstance() {
	return null;
    }

    /** Resets the chunker to work with a new input file */
    public static void reset(String f){
	EOF = false; 
	filename = f;
	try {
	    stream = new FileInputStream(filename);
	    in = new Scanner(stream);
	}
	catch (IOException e) {
	    System.err.println("Cannot open file " + filename);
	    e.printStackTrace();
	}
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
    
    public static Chunk getNextChunk() throws IOException {
	
	// if the Scanner has at least one more token to read, process
	if(in.hasNext()){
	    EOF = false;

	    byte[] data = new byte[chunk_size];
	    
	    // loop counter to keep track of position in buffer
	    int f = 0;

	    // read up to chunk_size tokens while possible
	    while(in.hasNext() && f < chunk_size){
	
		try{
		    String num = in.next();
		    data[f] = (byte) Integer.parseInt(num.substring(2, num.length()), 16);
		    f++;
		}
		catch(NumberFormatException e){
		    System.out.println("Oops");
		}
	    }

	    if (f == chunk_size){
		return new Chunk(chunk_size, data);
	    }
	    else {
		// fill the byte array with zeroes up to the end
		for (int i = f; i < data.length; i++) {
		    data[i] = 0;
		}
		stream.close();
		return new Chunk(chunk_size, data);
	    }
	}
	else {
	    EOF = true;
	    return new Chunk(chunk_size);
	}
    }
    
    /** Constructor with param: Needed since we don't want to hardcode in specific
     * filenames in here!
     */
    public Chunking(String f, int size) {
	// specify the chunk size here
	chunk_size = size;
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
    }
}
