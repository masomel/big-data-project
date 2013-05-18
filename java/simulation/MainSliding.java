package simulation;


public class MainSliding {

	/**
	 * This should the only one main function across all the files in the project. Do everything here.
	 * @param args
	 */
	public static void main(String[] args) {
		// Enter window size as argument when running the Simulator for stats purposes!
		int window_size = 0;
		String path = "";
		String website = "";
		int proxyCacheSize = 0;
		int mobileCacheSize = 0;
		int numIters = 0;

		if(args.length != 6){
		    System.out.println("Format: java SimulatorV2 [str: path-to-packet_bytes] [str: website] [+ int: window_size] [+ int: proxy cache size in chunks] [+ int: mobile cache size in chunks] [+ int: num iterations]");
		    System.exit(-1);
		}

		path = args[0];
		website = args[1];
		try {
		    window_size = Integer.parseInt(args[2]);
		    proxyCacheSize = Integer.parseInt(args[3]);
		    mobileCacheSize = Integer.parseInt(args[4]);
		    numIters = Integer.parseInt(args[5]);
		}
		catch(NumberFormatException e){
		    System.out.println("All numbers must be positive integers!!!!");
		    System.exit(-1);
		}
		
		// create a new simulator with the desired implementation
		ISimulator simulator = new SimulatorV2(path, website, window_size, proxyCacheSize, mobileCacheSize, numIters);
		simulator.simulate();
	}

}
