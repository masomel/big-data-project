package simulation;

import java.net.*;

public class MainNet {

	/**
	 * This should the only one main function across all the files in the project. Do everything here.
	 * @param args
	 */
	public static void main(String[] args) {
	    // Enter chunk size as argument when running the Simulator for stats purposes!
	    int port = 3000;
	    URL proxyLocation = null;
	    int chunk_size = 0;
	    int mobileCacheSize = 0;
	    
	    //check that correct number of command-line arguments has been entered
	    if(args.length < 4){
		System.err.println("Wrong format.");
		System.err.println("Format: java MobileClientNet <full URL of proxy> <port number> <cache size> <chunk size>");
		System.exit(-1);
	    }
	    
	    // get the url of the proxy location
	    try{
		proxyLocation = new URL(args[0]);
	    }
	    catch(MalformedURLException e){
		System.err.println("Wrong URL format. Needs to include http://");
		System.exit(-1);
	    }

	    // get the other numbers
	    try{
		port = Integer.parseInt(args[1]);
		mobileCacheSize = Integer.parseInt(args[2]);
		chunk_size = Integer.parseInt(args[3]);
	    }
	    catch(NumberFormatException e){
		System.err.println("port, mobile cache size, and chunk size must be positive ints.");
		System.exit(-1);
	    }
	    
	    // create a new simulator with the desired implementation
	    ISimulator simulator = new SimulatorV3(proxyLocation, port, mobileCacheSize, chunk_size);
	    simulator.simulate();
	}
    
}
