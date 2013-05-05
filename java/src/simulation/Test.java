package simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Test {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		
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
	}

}
