package simulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import chunking.Chunk;

public class Test {

	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		/*Subdirectories:
		appleStocks - apple
		cnn - cnn
		econ - econ
		googleStocks - google
		huffpost - huffpost
		nytimes - nytimes
		princeton - princeton
		stocks - finance
		weather - weather
		*/
		Data test = new Data(args[3]);
		test.getBytes();
		
		//Input parameters: chunk_size, proxyCacheSize, mobileCacheSize
		
		
		// Enter chunk size as argument when running the Simulator for stats purposes!
		int chunk_size = 0;
		int proxyCacheSize = 0;
		int mobileCacheSize = 0;

		if(args.length != 4){
		    System.out.println("Format: java SimulatorV1 [+ int: chunk_size] [+ int: proxy cache size in chunks] [+ int: mobile cache size in chunks] ");
		    System.exit(-1);
		}
		try {
		    chunk_size = Integer.parseInt(args[0]);
		    proxyCacheSize = Integer.parseInt(args[1]);
		    mobileCacheSize = Integer.parseInt(args[2]);
		}
		catch(NumberFormatException e){
		    System.out.println("All numbers must be positive integers!!!!");
		    System.exit(-1);
		}
		WebDataSimulator sim = new WebDataSimulator(chunk_size, proxyCacheSize, mobileCacheSize, args[3]);
		// create a new simulator with the desired implementation
		
		test.setSimulator(sim);
		test.RunExperiments();
	}
}
		
		
		/*
		while ((line = br.readLine()) != null) {
		   System.out.print(line);
		}
		br.close();
		*/
		
		
		/*
		try {
		    stream = new FileInputStream(filename);
		    in = new Scanner(stream);
		}
		catch (IOException e) {
		    System.err.println("Cannot open file " + filename);
		    e.printStackTrace();
		}
		if(in.hasNext()){
		    byte[] data = new byte[10];
		    while(in.hasNext()){
			try{
			    String content = in.next();
			    System.out.print(content);
			}
			catch(NumberFormatException e){
			    System.out.println("Oops");
			}
		    }
		}
		*/
	
		/*
		try {
 
			String content = "This is the content to write into file";
 
			File file = new File("score.txt");
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PrintWriter out = new PrintWriter("amazon1-recon.txt");
		out.println("hello");

		out.flush();
		out.close();
		*/
