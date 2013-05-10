package devices;
import java.net.*;
import java.io.*;
import java.util.*;

import processing.IProcessor;
import processing.SimpleProcessor;

import caching.ICache;
import caching.MRUCache;
import chunking.Chunk;
import chunking.ChunkingNet;
import devices.MobileNet;


/** 
 * 
 * @author Marcela Melara
 */
public class MobileClientNet {

    private static MobileNet mobile;
    private static int contentLen;
    private static int port;
    private static URL proxyLocation;
    private static int chunkSize;

    /** Initializes the mobile client
     */
    public static void initialize(URL pLoc, int p, int cacheSize, int chunk_size){

	proxyLocation = pLoc;	
	port = p;
	chunkSize = chunk_size;
	mobile = new MobileNet(cacheSize, chunkSize);

    } //ends initialize()

    public static MobileNet getMobile(){
	return mobile;
    }

    /** Sends the given list of needed chunks to the proxy server
     *
     */
    private static void sendNeededFps(ArrayList<Integer> needed, PrintStream w, URL u){
	
	w.println("NEEDED FPS "+u);
	w.println(needed.size());
	
	for(Integer i : needed){
	    w.println(i+"");
	}
	w.println();
	w.flush();
    } //ends sendNeededFps()

    public static void performRequestProtocol(URL web){

	// This is needed so each iteration works for the individual page
	mobile.resetLists();
	
	//attempt connection to proxy server
	try{
	    //socket to the proxy server
	    Socket socket = new Socket(/*proxy location*/proxyLocation.getHost(), /*port number*/port);
	    
	    //stream to send requests to proxy server
	    PrintStream out = new PrintStream(socket.getOutputStream());
	    
	    //send GET request to server
	    out.println("GET " +web+ " HTTP/1.1");
	    out.println();
	    out.flush();
	    
	    System.out.println("Client Message: Sent GET request to proxy server for "+web+".");
	    
	    //stream to receive messages from server
	    InputStream in =  socket.getInputStream();
	    
	    // Scanner to read the proxy server response
	    Scanner proxyResp = new Scanner(in);
	    
	    System.out.println("Client Message: Waiting to receive the fingerprints from the proxy.");
	    
	    String proxyHeader = proxyResp.nextLine();

	    // Receive the fingerprints if the proxy has sent the proper header line
	    if(proxyHeader.equals("ALL FPS "+web)){
		
		try{
		    contentLen = Integer.parseInt(proxyResp.nextLine());	    
		    
		    for(int i = 0; i < contentLen; i++){		   
			int fp = Integer.parseInt(proxyResp.nextLine());
			mobile.receiveAllFps(fp);		
		    }
		
		}
		catch(NumberFormatException e){
		    System.err.println("Fingerprints from proxy in wrong format!");
		    in.close();
		    out.close();
		    System.exit(-1);
		}
	    }
	    else{
		System.err.println("Some error occurred when receiving fingerprints from proxy.");
		System.exit(-1);
	    }
	     
	    System.out.println("Client Message: Received "+contentLen+" fingerprints from the proxy server.");

	    // compute needed fingerprints and send to proxy server
	    sendNeededFps(mobile.getNeededFps(), out, web);

	    System.out.println("Client Message: Sending needed fingerprints to proxy server.");
	    
	    // receive the needed chunk data according to the needed fingerprints

	    System.out.println("Client Message: Waiting to receive the chunk data for the needed chunks");

	    // This is needed in order to get the chunk data proxy header
	    String s = "";
	    while(!s.contains("NEEDED CHUNKS "+web)){	
		// Need to convert each incoming byte to a string since input is coming as text
		byte[] b = new byte[1];
		int f = in.read(b, 0, 1);
		s = s+new String(b);

		// all needed chunks responses have this header line
		if(s.contains("NEEDED CHUNKS "+web)){
		    break;
		}
	    }

	    // read in all the chunk data
	    for(int i = 0; i < contentLen; i++){
		byte[] data = new byte[chunkSize];
		int f = in.read(data, 0, chunkSize);

		if(f != chunkSize){
		    System.err.println("Some error occurred while receiving the needed chunk data from proxy.");
		    in.close();
		    out.close();
		    System.exit(-1);
		}
		else{
		    String str = new String(data);

		    if(str.contains('\00'+"") && (i != contentLen-1 ||
						  (i == contentLen -1 && str.indexOf('\00') == 0))){
			//System.out.println(i);
			mobile.receiveNeededChunks(null);
		    }
		    else{
			Chunk chunk = new Chunk(f, data);
			mobile.receiveNeededChunks(chunk);
		    }
		}
	    }

	    System.out.println("Client Message: Received all needed Chunks from the proxy");

	    System.out.println("Client Message: Reconstructing the webpage data");

	    mobile.reconstructData();

	    mobile.outputData(".",web.getHost());
	    
	    in.close();
	    out.close();
	    System.out.println("Client Message: Success! Request for "+web+" was finished proxy server.");
	}
	catch(IOException e){
	    System.err.println("Some error occured when trying to connect to Server.");
	    e.printStackTrace();
	    System.exit(-1);
	}
    } //ends performRequestProtocol()
    
} //ends MobileClientNet class
