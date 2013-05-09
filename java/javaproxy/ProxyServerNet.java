package javaproxy;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import processing.IProcessor;
import processing.SimpleProcessor;

import caching.ICache;
import caching.MRUCache;
import chunking.Chunk;
import chunking.ChunkingNet;
import fingerprinting.Fingerprinting;

public class ProxyServerNet{
    
    private static IProcessor proc;
    private static ICache cache;
    private static int chunkSize;

    public static ICache getCache(){
	return cache;
    }

     public static IProcessor getProcessor() {
    	return proc;
    }

    /** Initializes the Proxy server. Since there is only one proxy server, it can be static.
     * @param size the size of the proxy cache in number of chunks
     * @param cSize the number of bytes each chunk contains
     */
    private static void initialize(int size, int cSize){
	chunkSize = cSize;
	cache = new MRUCache(size); // Server cache holds size chunks
	proc = new SimpleProcessor();
    }

    /** MAIN
     * @param args the command-line arguments:
     * 			1. the port number for the server to listen at (must be >= 1024) 
     *                         2. the size of the proxy server's cache
     *                         3. the size of one chunk 
     */
    public static void main(String[] args) {
	
	//check that correct number of command-line arguments has been entered
	if(args.length < 3){
	    System.err.println("Wrong format.");
	    System.err.println("Format: java ProxyServerNet <port number> <cache size> <chunk size>");
	    System.exit(-1);
	}
	
	int port = 0, cacheSize = 0, chunk_size = 0; // for args
	
	try{
	    port = Integer.parseInt(args[0]); //the port number at which the server is listening
	    cacheSize = Integer.parseInt(args[1]); //the server's cache size in number of chunks
	    chunk_size = Integer.parseInt(args[2]); //the chunk size in number of bytes per chunk
	}
	catch(NumberFormatException e){
	    System.err.println("Arguments need to be positive ints!");
	    System.exit(-1);
	}

	// initialize the proxy server
	initialize(cacheSize, chunk_size);

	if(port < 1024){
	    System.err.println("Wrong port number.");
	    System.err.println("Port number must be greater than or equal to 1024.");
	    System.exit(-1);
	}
	
	ServerSocket serverSocket = null; //the ServerSocket
	
	//attempt to create a ServerSocket
	try{			
	    serverSocket = new ServerSocket(port);
	    System.out.println("Ready to listen for connections.");
	}
	catch(IOException e){
	    System.err.println("Some error occured while trying to create a ServerSocket.");
	    System.exit(-1);
	}
	
	//loop to listen for requests
	while(true){
	    
	    try{
		Socket clientSocket = serverSocket.accept(); //block for next connection
		
		System.out.println("ProxyServer accepted a new connection.");

		ServerThread th = new ServerThread(clientSocket);
		th.start(); //start a new Thread for the new connection
		
	    }
	    catch(IOException e){
		System.err.println("Main: Some error occured when trying to connect to the client.");
	    }	    
	    
	}
	
    } //ends main()
    
    /** A Thread that attempts a connection with a client
     * 
     * @author Marcela Melara
     *
     */
    private static class ServerThread extends Thread{
	
	private Socket clientSocket;

	private final int PORT = 80; // All requests are made to this port

	private ArrayList<Chunk> allChunks;
	private ArrayList<Integer> allFps;
	private ArrayList<Integer> neededFps;
	
	/** Constructor of a ServerThread
	 * 
	 * @param s the client socket
	 */
	public ServerThread(Socket s){
	    this.clientSocket = s;
	}
	
	/** Runs the ServerThread: calls the handle connection method
	 * 
	 * @param s the client socket
	 */
	public void run(){

	    // initialize the lists
	    allChunks = new ArrayList<Chunk>();
	    allFps = new ArrayList<Integer>();
	    neededFps = new ArrayList<Integer>();
	    
	    //attempt connection to the client
	    try{			
		InputStream in = clientSocket.getInputStream();
		OutputStream out = clientSocket.getOutputStream();

		Scanner read =  new Scanner(in); //scanner to read in from request
		
		String method = read.next(); //connection request method
		//System.out.println("Method: " +method);
		
		String urlStr = read.next(); //url of requested file
		String protocol = read.next(); //connection request protocol
		read.nextLine(); //get the empty line after the protocol

		URL url = null;

		try {
		    url = new URL(urlStr);
		} catch (MalformedURLException e1) {
		    System.err.println("Wrong URL format.");
		    return;
		}
		
		//System.out.println("urlStr: " +urlStr);
		
		//check if connection request protocol has been implemented
		if(!protocol.equals("HTTP/1.1")){
		    System.out.println("bad request");
		    return;
		}

		int contentLen = 0; // the length of the web content in number of chunks
		
		//check if connection request method has been implemented
		if(method.equals("GET")){

		    // actually go to the real web server of the requested URL and get the content
		    Socket proxyClientSocket = new Socket(url.getHost(), PORT);
		    PrintStream proxyClientOut = new PrintStream(proxyClientSocket.getOutputStream());
		    
		    //send GET request with proper user-agent to web server
		    sendGetRequest(proxyClientOut, url);

		    System.out.println("Proxy Message: Sent GET request to web server.");

		    //stream to receive messages from server
		    InputStream proxyClientIn =  proxyClientSocket.getInputStream();

		    // This is needed in order to disregard the HTTP response header
		    String s = "";
		    while(!s.contains("Connection: close")){
			// all responses have this last line
			if(s.contains("Connection: close")){
			    proxyClientIn.read();
			    break;
			}

			// Need to convert each incoming byte to a string since input is coming as text
			byte[] b = new byte[1];
			int f = proxyClientIn.read(b, 0, 1);
			s = s+new String(b);
		    }
		    		    
		    // partition incoming webpage into Chunks
		    if(proxyClientIn.available() > 0){
			allChunks = chunkWebData(proxyClientIn, chunkSize);    
		    }

		    System.out.println("Proxy Message: Finished chunking all the web server response.");

		    contentLen = allChunks.size();

		    proxyClientIn.close();

		}
		else{
		    System.out.println("Received bad request from mobile client.");
		    return;
		}
		    
		// compute the fingerprints for all received chunks
		allFps = getFingerprints(allChunks);
		
		System.out.println("Proxy Message: Finished computing fingerprints for all chunks.");
		
		PrintStream write = new PrintStream(out);
		
		// send the computed fingerprints to the mobile client
		sendAllFps(write, url);
		
		System.out.println("Proxy Message: Sent "+contentLen+" fingerprints to mobile device."); 
		
		System.out.println("Proxy Message: Waiting to receive needed fingerprints from mobile device.");

		String mobHeader = read.nextLine();
		mobHeader = read.nextLine();

		// receive needed fingerprints from mobile if the mobile device has sent the proper header line
		if(mobHeader.equals("NEEDED FPS "+url)){
		
		    try{
			int len = Integer.parseInt(read.nextLine());

			// check to see that the lengths are still in agreement
			if(len != contentLen){
			    System.err.println("Some error occurred when receiving needed fps from mobile.");
			    out.close();
			    in.close();
			    return;
			}
		    
			// get all the needed fingerprints
			for(int i = 0; i < contentLen; i++){		   
			    int fp = Integer.parseInt(read.nextLine());
			    if(fp == 0){
				neededFps.add(null);
			    }
			    else{
				neededFps.add(fp);		
			    }
			}
			
		    }
		    catch(NumberFormatException e){
			System.err.println("Fingerprints from mobile in wrong format!");
			out.close();
			in.close();
			return;
		    }
		}
		else{
		    System.err.println("Some error occurred when receiving needed fps from mobile.");
		    out.close();
		    in.close();
		    return;
		}
		
		System.out.println("Proxy Message: Finding the needed chunk data for the mobile device.");

		System.out.println("Proxy Message: Sending the needed chunk data to the mobile device.");
		
		// send the needed chunks to mobile client
		sendNeededChunks(prepareData(neededFps), out, url);

		in.close();
		out.close();

		System.out.println("Proxy Message: Success! Finished transaction with mobile client.");
		
	    }
	    catch(IOException e){
		System.err.println("Thread: Some error occured when trying to connect to client.");
		e.printStackTrace();
		System.exit(-1);
	    }
	    
	} //ends run()	

	/** Given an inputstream and a chunk size, generates chunks of the incoming data.
	 * @param in the InputStream which is sending the incoming data
	 * @param chunkSize the number of bytes each chunk contains
	 * @return a list of all the chunked data
	 */
	private ArrayList<Chunk> chunkWebData(InputStream in, int chunkSize){
	    ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	    ChunkingNet.setStream(in);
	    ChunkingNet.setChunkSize(chunkSize);
	    // read next chunks while the end of the file has not been reached
	    while(!ChunkingNet.isEOF()) {
	    	try {
		    Chunk c = ChunkingNet.getNextChunk();
		    
		    // ensure that we don't add empty chunk to our list
		    if(ChunkingNet.isEOF()){
			break;
		    }
		    chunks.add(c);
	    	} catch (IOException ioe) {
		    ioe.printStackTrace();
	    	}
	    }
	    return chunks;
	}
	
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
	} //ends getFingerprints()
	
	/** Given the list of all web data chunks and the list of the needed fingerprints,
	 * create a list of chunks to be sent over to the mobile device. A null entry indicates that
	 * the mobile device already has this chunk in its cache.
	 */
	private ArrayList<Chunk> prepareData(ArrayList<Integer> neededFps){
	
	    // Update cache before preparing the data for the mobile device
	    proc.processWebContent(allChunks, cache);
	    
	    ArrayList<Chunk> prepData = new ArrayList<Chunk>();
	    
	    if(allChunks.size() != neededFps.size()){
		System.out.println(allChunks.size());
		System.out.println(neededFps.size());
		System.out.println("Content and neededFps are not of the same length!");
		return null;
	    }
	    
	    for(int i = 0; i < allChunks.size(); i++){
		
		// check first to see if mobile device needs this chunk
		if(neededFps.get(i) == null){
		    prepData.add(null);
		}
		else{
		    int curFp = neededFps.get(i);
		    // check to see if we already have this chunk in our cache and if the mobile device needs it
		    if(cache.get(curFp) != null){
			prepData.add((Chunk)cache.get(curFp));
		    }
		    else if(cache.get(curFp) == null){
			prepData.add((Chunk)allChunks.get(i));
		    }
		}	    
		
	    }
	    
	    return prepData;
	    
	} //ends prepareData()
	
	/** Sends an http GET request through the given outputstream to the given URL
	 * @param o the outpustream through which to send the GET request
	 * @param u the URL to reach with the request
	 */
	private void sendGetRequest(PrintStream o, URL u){
	o.println("GET " +u+ " HTTP/1.1"); 
	o.println("Host: " +u.getHost());
	o.println("User-Agent: Mozilla/5.0 (Linux; U; Android 2.3; xx-xx; GT-I9100 Build/GRH78) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
	o.println("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	o.println("Accept-Language: en-US,en;q=0.5");
	o.println("Connection: keep-alive");
	o.println();
	o.flush();
	} //ends sendGetRequest()
	
	private void sendAllFps(PrintStream w, URL url){
	    w.println("ALL FPS "+url);
	    w.println(allFps.size());
	    
	    for(Integer i : allFps){
		w.println(i+"");
	    }
	    w.println();
	    w.flush();
	    
	} //ends sendAllFps()
	
	/** Sends the needed chunks data to the mobile client
	 * @param o the outputstream through which to send the chunk data
	 * @param u the url of the webpage the chunks belong to
	 */
	private void sendNeededChunks(ArrayList<Chunk> needed, OutputStream o, URL u)
	    throws IOException {
	    String header = "NEEDED CHUNKS "+u;
	    o.write(header.getBytes());
	    
	    for(Chunk c : needed){
		if(c == null){
		    byte [] nullByte = new byte[ChunkingNet.getChunkSize()];
		    o.write(nullByte);
		}
		else{
		    byte[] data = c.getData();
		    o.write(c.getData());
		}
	    }
	    o.write((int)'\n');
	    o.flush();
	} //ends sendNeededChunks()
	
    } //ends ServerThread class
    
} //ends ProxyServerNet class