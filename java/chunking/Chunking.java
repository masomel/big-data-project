package chunking;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

import fingerprinting.Fingerprinting;

public class Chunking {

    /* private static final Chunking instance = new Chunking(); */
    private static FileInputStream stream;
    private static Scanner in; // use Scanner for now since input files are .txt format
    private static int chunk_size;
    private static String filename;
    private static boolean EOF; // flag to mark when the end of a file has been reached
    private static int window_size; //replaces chunk_size in getNextChunkRolling()
    private static int contentLen;

    //Constants
    public static final int DEFAULT_WINDOW_SIZE = 4;

    private static final int MAX_CHUNK_SIZE = 50;
    private static final int MIN_CHUNK_SIZE = DEFAULT_WINDOW_SIZE+1; //should be the min 

    // needed to find breakpoint
    private static int gamma = 31; //represents gamma from Spring/Wetherall paper 2^5    

    public static Chunking getInstance() {
	return null;
    }

    public static String getFilename(){
	return filename;
    }

    public static int getChunkSize(){
	return chunk_size;
    }

    public static int getContentLength(){
	return contentLen;
    }

    public static boolean isEOF(){
	return EOF;
    }
    
    public static void setChunkSize(int size){
    	chunk_size=size;
    }

    public static void setWindowSize(int size){
	window_size = size;
    }
    
    /**  param: Needed since we don't want to hardcode in specific
     * filenames in here!
     */
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
    } //ends getNextChunk()

    /** Chunks an entire file using sliding window
     *
     */
    public static ArrayList<Chunk> getAllChunksSliding() throws IOException {

	// list of chunks
	ArrayList<Chunk> chunks = new ArrayList<Chunk>();

	// read in entire file
	ArrayList<Byte> fileBytes = new ArrayList<Byte>();

	while(in.hasNext()){
	    
	    try{
		String num = in.next();
		fileBytes.add((byte) Integer.parseInt(num.substring(2, num.length()), 16));
	    }
	    catch(NumberFormatException e){
		System.out.println("Oops");
	    }
	}

	contentLen = fileBytes.size();

	System.out.println("# bytes in file: "+contentLen);
	
	int lastbp = 0; // the last breakpoint in the file

	// slide the window across the file bytes until reaching the end
	for(int offset = 0; offset <= contentLen-window_size;){

	    byte[] window = new byte[window_size];
	    for(int i = offset; i < window_size; i++){
		window[offset-i] = fileBytes.get(i);
	    }
	    
	    int fp = Fingerprinting.fingerprint(window);
	    byte[] data = new byte[MIN_CHUNK_SIZE];

	    int isBp = fp | gamma;
	    
	    chunk_size = offset + window_size - lastbp;

	    if(chunk_size == MAX_CHUNK_SIZE){
		data = (byte[]) fileBytes.subList(lastbp, lastbp+chunk_size-1).toArray();
		chunks.add(new Chunk(chunk_size, data));
		lastbp = offset + window_size;
		offset = lastbp;
	    }
	    // if this is true, we have found our chunk boundary
	    else if(isBp == 0){
		
		if(chunk_size >= MIN_CHUNK_SIZE && chunk_size < MAX_CHUNK_SIZE){
		    
		    int rem = contentLen - (offset+window_size);
		    if(rem < MIN_CHUNK_SIZE){
			chunk_size = chunk_size + rem;
		    }

		    data = fileBytes.subList(lastbp, lastbp+chunk_size-1).toArray();
		    chunks.add(new Chunk(chunk_size, data));
		    lastbp = offset + window_size;
		    offset = lastbp;
		}
		else{
		    offset++;
		}
	    }
	    else{
		offset++;
	    }

	    System.out.println("chunk size: "+chunk_size);
	    System.out.println("last bp: "+lastbp);
	    System.out.println("offset: "+offset);

	}

	return chunks;

    } //ends getNextChunkRolling()    

}
