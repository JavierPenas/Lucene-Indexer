package es.udc.fi.ri.mri_searcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.index.Term;

public class Utilities {
	public static int ValidateName(String s){
		if (s.endsWith(".1400")){
			System.out.println("file: "+s);
			return 1;
		}else{
			return 0;
		}
	}
	
	public static List<Entry<String,Double>> orderForMax(Map map){
		Set<Entry<String,Double>> set = map.entrySet();
		List<Entry<String,Double>> list = new ArrayList<Entry<String, Double>>(set);
		
		Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
        {
            public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
		return list;
		
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
