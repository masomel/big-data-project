package simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

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

public class Data {
	private String month;
	private int date;
	private String[] timeofday;
	private String[] regVmob;
	private String pathToData;
	private String srcpath;
	private String[] websites;
	private WebDataSimulator sim;
	
	
	public Data(String path){
		month = "May";
		date=0;
		timeofday = new String[3];
		regVmob = new String[2];
		pathToData = path + "content/";
		srcpath = path + "htmlToByte/";
		websites = new String[9];
	
		timeofday[0] = "Morn";
		timeofday[1] = "After";
		timeofday[2] = "Eve";
		
		regVmob[0] = "";
		regVmob[1] = "Mobile";
		
	
		websites[0]="appleStocks/apple";
		websites[1]="cnn/cnn";
		websites[2]="econ/econ";
		websites[3]="googleStocks/google";
		websites[4]="huffpost/huffpost";
		websites[5]="nytimes/nytimes";
		websites[6]="princeton/princeton";
		websites[7]="stocks/finance";
		websites[8]="weather/weather";
	}
	
	
	public void getBytes(){
		// for each website
			//for each day
				//for each time
					//Mob vs reg
		int weblen = websites.length;
		int timlen = timeofday.length;
		for(int a=0; a<weblen; a++){
			for(int b=11; b<15; b++){
				for(int c=0; c<timlen; c++){
					for(int d=0; d<2; d++){
						try {
							htmlToByte(websites[a], b, timeofday[c], regVmob[d]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	

	public void htmlToByte(String website, int date, String time, String regOrMob) throws IOException{
		String filename = website + month + date + time + regOrMob;
		String ip = "";
	    BufferedReader reader = new BufferedReader( new FileReader (pathToData + filename));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");
	    boolean payload = false;
	    //Read File without header
	    while( ( line = reader.readLine() ) != null) {
	    	if(line.startsWith("Trying ")){
	    		ip = line.substring(7, line.indexOf("..."));
	    	}
	    	if(line.equals("")){
	    		payload = true;	
	    		continue;
	    		//can start collecting data.
	    		//System.out.println(i);
	    		//break;
	    	}
	    	if(payload == false)
	    		continue;
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }
	    
	    //Convert to Bytes
	    String payloadData = stringBuilder.toString();
	    byte[] payloadBytes = payloadData.getBytes("UTF-16");
	   	//PrintWriter out;
	   	PrintWriter out2;
	   	//Create directory and file
	   	//System.out.println(website.substring(0, website.indexOf('/') + 1));
	   	String sourcepath = srcpath + website.substring(0, website.indexOf('/') + 1);
	   	String nameoffile = website.substring(website.indexOf('/') + 1) + month + date + time + regOrMob;
		try {
			File dir = new File(srcpath);
			dir.mkdir();
			File dir1 = new File(sourcepath);
			dir1.mkdir();
			/* Debugging
			out = new PrintWriter(srcpath + "test");
	    	out.println(payloadData);
	    	out.flush();
	    	out.close();
	    	*/
	    	out2 = new PrintWriter(sourcepath + nameoffile);
	    	for(int i=0; i<payloadBytes.length; i++){
	       	    String hex = Integer.toHexString((int)payloadBytes[i]);
	       	    if(i==payloadBytes.length-1)
	       	    	break;
	       	    if(hex.length() > 2){
	       	    	out2.println("0x" + hex.substring(hex.length()-2, hex.length()));
	     	    }
	     	    else if(hex.length() == 1){
	     	    	 out2.println("0x" + "0" + hex);
	    	    }
	    	    else if(hex.length() == 2){
	    	    	out2.println("0x" + hex);
	    	    }
	    	    else{
	    	    	out2.print("");
	    	    }
	    	}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
	public void setSimulator(WebDataSimulator sim){
		this.sim = sim;
	}
	
	public void RunExperiments(){
		// for each website
			//for each day
				//for each time
					//Mob vs reg
		sim.setOutputFile();
		int weblen = websites.length;
		int timlen = timeofday.length;
		for(int a=0; a<weblen; a++){
			for(int b=11; b<15; b++){
				for(int c=0; c<timlen; c++){
					for(int d=0; d<2; d++){
						//System.out.println(srcpath + websites[a] + month + b + timeofday[c] + regVmob[d]);
						sim.runProtocol(srcpath + websites[a] + month + b + timeofday[c] + regVmob[d]);
					}
				}
			}
		}
		
		sim.printAvgMissRate();
		sim.closeOutputFile();
	}
	
	
	
	
	}
