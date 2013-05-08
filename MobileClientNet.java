import java.net.*;
import java.io.*;
import java.util.*;

/** Web client to allow the user to retrieve web files from a remote machine. 
 * It does however, not display these files, but you can then display them with an existing browser by clicking on "Open File" in the file menu and then navigating to
 * the retrieved file.  
 * 
 * @author mm4966
 */
public class MobileClientNet {
	
	/** MAIN
	 * @param args the command-line arguments: 
	 * 			1. URL to the web file (only http allowed)
	 * 			2. port number corresponding WebServer listens at (must be over 1024)
	 */
	public static void main(String[] args){
		
		//check that correct number of command-line arguments has been entered
		if(args.length < 3){
			System.err.println("Wrong format.");
			System.err.println("Format: java WebClient <full URL of proxy> <full URL of web server> <port number>");
			System.exit(-1);
		}
		
		URL url = null; //the URL entered by the user
		URL web = null;
		//attempt creating the new URL, or exit program if format is wrong
		try {
			url = new URL(args[0]);
			web = new URL(args[1]);
		} catch (MalformedURLException e1) {
			System.err.println("Wrong URL format.");
			System.exit(-1);
		}
		
		//check that URL argument is HTTP
		if(web!=null && !web.getProtocol().equals("http")){
			System.err.println("Wrong protocol.");
			System.err.println("Protocol: http");
			System.exit(-1);			
		}		
		 
		//attempt connection to server
		try{
			//socket to the server
			Socket socket = new Socket(/*host*/url.getHost(), /*port number*/Integer.parseInt(args[2]));
			
			//stream to send requests to server
			PrintStream out = new PrintStream(socket.getOutputStream());
			
			//the path to the requested file name; required to obtain file extension
			//String filePath = web.getFile(); 
			//System.out.println(filePath);
			
			//get the file extension to decide how to deal with requested file
			String fileExt = "Blah"/*getFileExtension(filePath)*/; 			
			
			//File reqFile = new File(filePath); //requested file --> only used to obtain file name
			String fileName = "Foof"/*reqFile.getName()*/; //the filename of the requested file
			
			//send GET request to server
			out.println("GET " +web+ " HTTP/1.1"); 
			out.println("Host: " +web.getHost());
			out.println();
			
			System.out.println("Client Message: Sent GET request to server.");
			
			//stream to receive messages from server
			InputStream in =  socket.getInputStream();
			
			System.out.println("Client Message: Requested file name: " +fileName);
			
			/* wrong file type */
			if(fileExt == null){
			    System.out.println("File type of requested file not supported.");
			    System.out.println("Supported file types: html, css, jpg (or jpeg), gif");
			    System.exit(-1);
			}
			
			/* requested file type supported:
			 * handle text files and picture files separately by using different Streams to 
			 * read and write file contents */
			else{
				//create file to save requested file
				File retFile = new File("/home/marcela/" +fileName);
				
				//Scanner to read the server response
				Scanner serverResp = new Scanner(in);
				
				String[] header;
				if(url.getHost().equals("math.hws.edu")){
					header = new String[9];
				}
				else{
					header = new String[5];
				}
				
				for(int i = 0; i < header.length; i++){
					header[i] = serverResp.nextLine();
				}
				
				//checks server response for OK
				if(header[0].equals("HTTP/1.1 200 OK")){
					System.out.println("Request granted.");
				
					//file to be retrieved is a text file
					if(fileExt.equalsIgnoreCase("HTML") || fileExt.equalsIgnoreCase("CSS")){ 

						//the outputStream to write the incoming file as text to the retrieved file
						PrintWriter txtFileOut = new PrintWriter(retFile); 
					
						/* while the Scanner has tokens, read in the next line, and write the line to the
						   file through the PrintWriter */
						while (serverResp.hasNextLine()){
							String inText = serverResp.nextLine();					
							txtFileOut.println(inText);
							System.out.println(inText);
						}
				
						//file has been successfully written if we get here
						System.out.println("Client Message: Finished retrieving file.");
				
						//close both Streams
						serverResp.close();
						txtFileOut.close();
				
					} //ends 'text file' case 
				
					//file to be retrieved is a picture file
					else if(fileExt.equalsIgnoreCase("JPEG") || fileExt.equalsIgnoreCase("JPG") || fileExt.equalsIgnoreCase("GIF")){
					
						// using InputStream directly since picture files are stored in raw bytes
						InputStream picIn = in;
												
						//the outputStream to write the incoming file as raw bytes to the retrieved file
						FileOutputStream picOut = new FileOutputStream(retFile);
					
						/* while InputStream has bytes, read in the next byte, and write it to the file 
						 * as raw bytes through the FileOutputStream */
						while(picIn.available() > 0 ){
							byte picByte = (byte) in.read();						
							picOut.write(picByte);
						}
					
						//file has been successfully written if we get here
						System.out.println("Client Message: Finished retrieving file.");
				
						//close both Streams
						in.close();
						picOut.close();
				    
					} //ends 'picture file' case
				}
				else{
					System.out.println("Your request was denied by server.");
					System.out.println("Error: " +header[0]);
				}//ends 'check server response' if-statements
				
			}//ends 'file type'if-statements
			
		}
		catch(IOException e){
			System.err.println("Some error occured when trying to connect to Server.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		
	} //ends main()
	
	
	/** gets the file extension from a given filepath and returns the extension
	 * 
	 * @param filepath the path to the file whose extension is to be determined
	 * @return the file extension (without period)
	 */
	private static String getFileExtension(String filepath){
		
		String extension = ""; //file extension
		
		/* if 5th character from the right (i.e. reading the end of the filename string)
		 * is a period '.' then file extension is 4 characetrs long (e.g. html) */
		if(filepath.charAt(filepath.length()-5)=='.'){			
			extension = filepath.substring(filepath.length()-4, filepath.length());
		}
		
		/* if 4th character from the right (i.e. reading the end of the filename string)
		 * is a period '.' then file extension is 4 characetrs long (e.g. jpg) */
		else if(filepath.charAt(filepath.length()-4)=='.'){
			extension = filepath.substring(filepath.length()-3, filepath.length());
		}
		
		/* file extension is either too long or too short */
		else{
			extension = null;
		}
		
		return extension;
		
	} //ends getFileExtension() 

} //ends WebClient class
