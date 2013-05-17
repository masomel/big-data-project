package simulation;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.*;

import caching.ICache;
import chunking.Chunk;
import chunking.ChunkingNet;
import devices.MobileNet;
import devices.MobileClientNet;

/**
 * Class representing the first version of the simulator.
 * @author Marcela Melara, Nayden Nedev
 *
 */
public class SimulatorV3 implements ISimulator {
    
    public SimulatorV3(URL proxyLoc, int port, int mobileCacheSize, int chunk_size) {
	MobileClientNet.initialize(proxyLoc, port, mobileCacheSize, chunk_size);
    }

    @Override
	public void simulate() {
	
	String isCont = "y";
	
	String requestedPage = "";
	URL url = null;

	MobileNet mobile = MobileClientNet.getMobile();

	int numIters = 0;
	int mobSum = 0;

	Scanner in = new Scanner(System.in);
	
	while(isCont.equalsIgnoreCase("y")){
	    numIters++;
	    
	    System.out.println("Please enter the url of the next page you wish to visit.");
	    System.out.print(">> ");
	    
	    requestedPage = in.nextLine();
	    
	    try{
		url = new URL("http://"+requestedPage+"/");
	    }
	    catch(MalformedURLException e){
		System.err.println("Wrong URL format.");
		return;
	    }
	    
	    MobileClientNet.performRequestProtocol(url);
	    
	    System.out.println("----------------------");
	    
	    System.out.println("Remaining mobile cache capacity: "+mobile.getCache().getCapacity());
	    System.out.print("Mobile missrate: ");
	    customFormat("##.####", mobile.getProcessor().getMissRate());
	    System.out.println();
	    
	    mobSum += mobile.getProcessor().getMissRate();
	    
	    System.out.print("Would you like to continue? [y/n] >> ");
	    
	    isCont = in.nextLine();
	    
	    if(isCont.equalsIgnoreCase("n")){
		break;
	    }
	    else if(!isCont.equalsIgnoreCase("y")){
		System.out.println("Wrong response. Wil continue for another round.");
		isCont = "y";
	    }
	    
	}		
	
	System.out.print("Avg mobile missrate over all inspected sites: ");
	customFormat("##.####", (double)mobSum/numIters);
	
    }
    
    /** Helper function:
     * Formats how the doubles are printed given a specific pattern
     */
    public static void customFormat(String pattern, double value ) {
	DecimalFormat myFormatter = new DecimalFormat(pattern);
	String output = myFormatter.format(value);
	System.out.println(output);
    }
    
}
