package es.udc.fi.ri.mri_searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CranfieldParser {
	
	//ABRE EL DOCUMENTO QUE CONTIENE LA COLECCION
	private static InputStreamReader openDoc(File file) throws FileNotFoundException{
		FileInputStream fis = null;
		fis = new FileInputStream(file);
		
		if(fis==null)
			return null;
		else
			return new InputStreamReader(fis, StandardCharsets.UTF_8);
	}
	
	//LEE EL DOCUMENTO CON LA COLECCION LINEA A LINEA
	private static StringBuffer readFile(File file) throws Exception{
		InputStreamReader fis;
		fis = openDoc(file);
		BufferedReader buffer = new BufferedReader(fis);
        StringBuffer filecontent = new StringBuffer();
        String text = null;
        
        while((text=buffer.readLine()) != null){
      	  filecontent.append(text).append("\n");
        }
        return new StringBuffer(filecontent);
	}
	
	//PARSER, ASUMIENDO QUE LOS DOCUMENTOS CUENTAN CON TODOS LOS CAMPOS Y QUE .A y .B SIEMPRE TIENEN UNA SOLA LINEA
	public static List<List<String>> parseString(StringBuffer fileContent) throws Exception{
		/* CONVERTIMOS EL CONTENIDO EN UN STRING */
		//StringBuffer fileContent = readFile(file);

		String text = fileContent.toString();
		String[] lines = text.split("\n"); //Dividimos el contenido del fichero en lineas
		List<List<String>> documents = new LinkedList<List<String>>();
		List<String> document = new LinkedList<String>();
		
		//PARA CADA DOCUMENTO, DIVIDIMOS SUS PARTES Y ALMACENAMOS
		for (int i = 0; i < lines.length; ++i) {
			if(lines[i].startsWith(".I")){
				document = new LinkedList<String>();
				document.add(lines[i].substring(3));
			}else if (lines[i].startsWith(".T")){ 
				i++;
				StringBuffer tBuffer = new StringBuffer();
				while (!(lines[i].startsWith(".A")||lines[i].startsWith(".B")||lines[i].startsWith(".W"))){
					tBuffer.append(lines[i]);
					tBuffer.append("\n");
					i++;
				}
				document.add(tBuffer.toString());
			}else if (lines[i].startsWith(".A")){
				i++;
				document.add(lines[i]);
			}else if (lines[i].startsWith(".B")){
				i++;
				document.add(lines[i]);
			}else if (lines[i].startsWith(".W")){
				i++;
				StringBuffer wBuffer = new StringBuffer();
				while (  (i<lines.length) && (!lines[i].startsWith(".I")) ){
					wBuffer.append(lines[i]);
					wBuffer.append("\n");
					i++;
				}
				document.add(wBuffer.toString());
				documents.add(document);
				//document.clear();
			}
		}
		
		return documents;

	}
	
	
	
}
