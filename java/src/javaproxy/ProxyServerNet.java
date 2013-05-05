package javaproxy;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import caching.SimpleCache;
import chunking.Chunk;
import fingerprinting.Fingerprinting;

public class ProxyServerNet{
    
    private SimpleCache cache;
    private ArrayList<Chunk> allChunks;
    private ArrayList<Integer> allFps;
    private ArrayList<Integer> neededFps;
    private byte[] webcontent;

    public ProxyServerNet(int size){
	cache = new SimpleCache(size); // Server cache holds size chunks
	allChunks = new ArrayList<Chunk>();
	allFps = new ArrayList<Integer>();
	neededFps = new ArrayList<Integer>();
    }

    public void receiveNeededFps(ArrayList<Integer> needed){
	neededFps = needed;
    }

    public ArrayList<Integer> sendAllFps(ArrayList<Chunk> content){
	allChunks = content;
	allFps = getFingerprints(content);
	return allFps;
    }

    public ArrayList<Chunk> sendNeededChunks(){
	return prepareData(neededFps);
    }

    public SimpleCache getCache(){
	return cache;
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
    }

    /** Given the list of all web data chunks and the list of the needed fingerprints,
     * create a list of chunks to be sent over to the mobile device. A null entry indicates that
     * the mobile device already has this chunk in its cache.
     */
    private ArrayList<Chunk> prepareData(ArrayList<Integer> neededFps){
	
	// Update cache before preparing the data for the mobile device
	cache.processWebContent(allChunks);
	
	ArrayList<Chunk> prepData = new ArrayList<Chunk>();
	
	if(allChunks.size() != neededFps.size()){
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

    /** MAIN
     * @param args the command-line arguments:
     * 			1. the port number for the server to listen at (must be >= 1024) 
     */
    public static void main(String[] args) {
	
	//check that correct number of command-line arguments has been entered
	if(args.length < 1){
	    System.err.println("Wrong format.");
	    System.err.println("Format: java ProxyServerNet <port number>");
	    System.exit(-1);
	}
	
	int port = Integer.parseInt(args[0]); //the port number at which the server is listening
	
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
	    
	    //attempt connection to the client
	    try{
		InputStream in = clientSocket.getInputStream();
		PrintStream out = new PrintStream(clientSocket.getOutputStream());
		
		Scanner read = new Scanner(in); //scanner to read in from request
		
		String method = read.next(); //connection request method
		System.out.println("Method: " +method);
		
		String urlStr = read.next(); //url of requested file
		String protocol = read.next(); //connection request protocol
		
		System.out.println("urlStr: " +urlStr);
		
		//check if connection request protocol has been implemented
		if(!protocol.equals("HTTP/1.1")){			
		    //sendError(out, BAD_REQUEST);
		    return;
		}
		
		//check if connection request method has been implemented
		if(method.equals("GET")){

		    // actually go to the real web server of the requested URL and get the content
		    Socket proxyClientSocket = new Socket(urlStr, PORT);

		    out = new PrintStream(proxyClientSocket.getOutputStream());
		    
		    URL url = new URL(urlStr);
		    //send GET request to web server
		    out.println("GET /" +urlStr+ " HTTP/1.1"); 
		    out.println("Host: " +url.getHost());
		    out.println();

		    System.out.println("Proxy Message: Sent GET request to web server.");

		    //stream to receive messages from server
		    InputStream in1 =  proxyClientSocket.getInputStream();
		    
		    // partition incoming webpage into Chunks
		    while(in1.available() > 0){
			// TODO: need to make networked version of Chunking, i.e. using Inputstream from
			// Socket instead of File
		    }

		    // compute the fingerprints for all received chunks
		    
		    // send the computed fingerprints to the mobile client
		    // TODO: need to convert array list of fingerprints to some sort of buffer that can be 
		    //             sent via network
		  
		}
		else{
		    // Mobile client is sending needed fingerprints!
		    // TODO: implement this

		    // receive needed fingerprints from mobile
		    // TODO: convert received data to ArrayList of fingerprints again

		    // get the needed chunks

		    // send the needed chunks to mobile client
		    // TODO: convert chunks to array of bytes or some sort of buffer
		}
		
	    }
	    catch(IOException e){
		System.err.println("Thread: Some error occured when trying to connect to client.");
		System.exit(-1);
	    }
	    
	} //ends run()	
	
    } //ends ServerThread class
    
} //ends ProxyServerNet class