package simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import caching.ICache;
import chunking.Chunk;
import chunking.Chunking;
import devices.Mobile;
import devices.ProxyServer;

public class WebDataSimulator2 {

	private ProxyServer proxy;
	private Mobile mobile;
	private double proxySum;
	private double mobSum;
	private int counter;
	private String datapath;
	private int mobprox;
	private int proxserv;
	private int singlemobprox;
	private int singleproxserv;
	private PrintWriter out;
	
	
	int chunk_size = 0;
	int proxyCacheSize = 0;
	int mobileCacheSize = 0;
	
	public WebDataSimulator2(int chunk_size,int proxyCacheSize,int mobileCacheSize, String datapath){
		this.chunk_size = chunk_size;
		this.proxyCacheSize = proxyCacheSize;
		this.mobileCacheSize = mobileCacheSize;
		this.counter=0;
		proxy = new ProxyServer(proxyCacheSize); //holds 2000 chunks; TODO: this should be an argument
		mobile = new Mobile(mobileCacheSize); //holds 500 chunks; TODO: this should be an argument
		proxySum = 0;
		mobSum = 0;
		this.mobprox=0;
		this.proxserv=0;
		this.singlemobprox=0;
		this.singleproxserv=0;
		this.datapath=datapath;
	}
	
	public void setCounter(int num){
		counter=num;
	}
	
	public void setOutputFile(){
		try {
			out = new PrintWriter(datapath + "output2.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void closeOutputFile(){
		out.flush();
		out.close();
	}

	
	public void runProtocol(String path) {
		counter++;
		// Used to calculated the average missrates
		singlemobprox=0;
		singleproxserv=0;
		// The Chunking Facility		
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		chunks = proxy.chunkWebData(path, chunk_size);
	//	out.println("");
	    out.println("Processing file: " + Chunking.getFilename());
	   // out.println("");
	    if(chunks.size()==0)
	    	return;
	    updateProxServ("SERVER TO PROXY", chunks.size()*chunk_size);
	    //SEND ALL FPS TO MOBILE
	    ArrayList<Integer> fps = proxy.sendAllFps(chunks);
	    mobile.receiveFps(fps);
	    int allfps = fps.size();
	    updateMobProx("ALL FPS PROXY TO MOB", allfps*4);
	    //MOBILE SEND SNEEDED FPS TO PROXY
	    ArrayList<Integer> mobNeeded = mobile.sendNeededFps();
	    proxy.receiveNeededFps(mobNeeded);
	    int numFPsNeeded = mobile.getNumNeededFps();
	    updateMobProx("SOME FPS MOB TO PROX",  numFPsNeeded*4);
	    
	    // PROXY SENDS NEEDED CHUNKS TO MOBILE
	    ArrayList<Chunk> neededChunks = proxy.sendNeededChunks();
	    mobile.receiveChunks(neededChunks);
	    int numChunksNeeded = proxy.getNumNeededChunks();
	    updateMobProx("SOME CHUNKS PROXY TO MOB", numChunksNeeded*chunk_size);
	    out.print(singlemobprox + "\t");
	    out.print(mobprox + "\t");
	    //HITRATE:
	    int hitnum = allfps - numFPsNeeded;
	    double hitrate = (double)hitnum/allfps;
	    //MISRATE: 
	    int missnum = numFPsNeeded;
	    double missrate = (double)missnum/allfps;
	    out.print(missrate + "\t");
	    
	    
	    /*
	    out.print("hitnum: " +hitnum + " hitrate: ");
	    customFormat("##.####", hitrate);
	    out.print("missnum: " +missnum + " missrate: ");
	    customFormat("##.####", missrate);
	    */
	    
	    
	    try {
	    	byte[] webdata = mobile.reconstructData();
	    	String path2 = datapath;
	    	String filename = path.substring(path.lastIndexOf('/') + 1);
	    	mobile.outputData(webdata, path2, filename);
	    } catch (FileNotFoundException fnfe) {
	    	fnfe.printStackTrace();
	    }
	    
	    /*
	    out.print("----------------------");
	    
	    out.println("Number of chunks inspected: "+chunks.size());	
	    out.println("Remaining proxy cache capacity: "+proxy.getCache().getCapacity());
	    out.print("Proxy missrate: ");
	    customFormat("##.####", proxy.getProcessor().getMissRate());
	    out.println("Remaining mobile cache capacity: "+mobile.getCache().getCapacity());
	    out.print("Mobile missrate: ");
	    customFormat("##.####", mobile.getProcessor().getMissRate());
	    out.println("");
	    out.println("TOTAL BYTES MOB-PROX THIS WEBSITE: " + singlemobprox);
	    out.println("TOTAL BYTES PROX-SERVER THIS WEBSITE: " + singleproxserv);
	    */
	    
	    
	    mobile.getProcessor().processWebContent(neededChunks, mobile.getCache());
	    proxySum += proxy.getProcessor().getMissRate();
	    mobSum += mobile.getProcessor().getMissRate();		
	    customFormat("##.####", proxy.getProcessor().getMissRate());
	    customFormat("##.####", mobile.getProcessor().getMissRate());
	}
	
	public void printAvgMissRate(){
		out.print("Avg proxy missrate over all inspected sites: ");
		customFormat("##.####", proxySum/counter);
		out.print("Avg mobile missrate over all inspected sites: ");
		customFormat("##.####", mobSum/counter);
	}
	
	
	public void updateProxServ(String desc, int num){
		proxserv+=num;
		singleproxserv+=num;
		/*
		out.println(desc);
		out.println("BYTES SERV-PROXY: " + num);
		out.println("THIS WEBSITE SERV-PROX: " + singleproxserv);
		out.println("TOTAL BYTES SERV-PROX: " + proxserv);
		out.println("----------------------");
		*/
		out.print(num + "\t");
	}
	
	
	
	public void updateMobProx(String desc, int num){
		mobprox+=num;
		singlemobprox+=num;
		out.print(num + "\t");
		/*
		out.println(desc);
		out.println("BYTES MOB-SERV: " + num);
		out.println("THIS WEBSITE MOB-PROX: " + singlemobprox);
		out.println("TOTAL BYTES MOB-SERV: " + mobprox);
		out.println("----------------------");
		*/

	}
        
    private static Chunk[] FPArrayListToChunkArray(ArrayList<Integer> fp, ICache cache){
	int len = fp.size();
	Chunk[] chunks = new Chunk[len];
	for(int i=0; i<len; i++){
	    chunks[i] = (Chunk)(cache.get((Integer)(fp.get(i))));
	}
	return chunks;
	
    }

    /** Helper function:
     * Formats how the doubles are printed given a specific pattern
     */
    public void customFormat(String pattern, double value ) {
	DecimalFormat myFormatter = new DecimalFormat(pattern);
	String output = myFormatter.format(value);
	out.print(output + "\t");
    }
    
}
