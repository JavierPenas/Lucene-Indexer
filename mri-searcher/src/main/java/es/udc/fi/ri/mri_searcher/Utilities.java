package es.udc.fi.ri.mri_searcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Utilities {
	public static int ValidateName(String s){
		if (s.endsWith(".1400")){
			System.out.println("file: "+s);
			return 1;
		}else{
			return 0;
		}
	}


	public static InputStreamReader openDocument(File file){
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
			InputStreamReader in = new InputStreamReader(input, StandardCharsets.UTF_8);
			return in;
		} catch (FileNotFoundException e) {
			return null;
		}
		
		
	}
}
