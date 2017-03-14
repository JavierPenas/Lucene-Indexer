package es.udc.fi.ri.mri_indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.lucene.document.DateTools;

public class Utilities {
	public static int ValidateName(String s){
		Pattern p = Pattern.compile("reut2-\\d{3}.sgm");
		if (p.matcher(s).matches()){
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
	
	public static String parseDate(String s){
		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS");
		try {
			Date date = format.parse(s);
			String luceneDate = DateTools.dateToString(date, DateTools.Resolution.MILLISECOND);
			return luceneDate;
		} catch (ParseException e) {
			s = "1-FEB-1900 24:00:00.51";
			try {
				Date date =  format.parse(s);
				String luceneDate = DateTools.dateToString(date, DateTools.Resolution.MILLISECOND);
				return luceneDate;
			} catch (ParseException e1) {
				System.out.println("IMPOSIBLE TO PARSE THE DATE");
			}
		}
		return null;
	}
}
