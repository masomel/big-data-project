package simulation;

import simulation.*;

public class Main {

	/**
	 * This should the only one main function across all the files in the project. Do everything here.
	 * @param args
	 */
	public static void main(String[] args) {
		// Enter chunk size as argument when running the Simulator for stats purposes!
		int chunk_size = 0;
		String path = "";

		if(args.length != 2){
		    System.out.println("Format: java SimulatorV1 [string: path] [postive int: chunk_size]");
		    System.exit(-1);
		}

		path = args[0];
		try {
		    chunk_size = Integer.parseInt(args[1]);
		}
		catch(NumberFormatException e){
		    System.out.println("Chunk size must be positive integer!!!!");
		    System.exit(-1);
		}
		
		// create a new simulator with the desired implementation
		ISimulator simulator = new SimulatorV1(path, chunk_size);
		simulator.simulate();
	}

}
