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
	

	//PARSER, ASUMIENDO QUE LOS DOCUMENTOS CUENTAN CON TODOS LOS CAMPOS Y QUE .A y .B SIEMPRE TIENEN UNA SOLA LINEA
	public static List<String> parseDocument(StringBuffer documentContent) throws Exception{
		/* CONVERTIMOS EL CONTENIDO EN UN STRING */
		//StringBuffer fileContent = readFile(file);

		String text = documentContent.toString();
		String[] lines = text.split("\n"); //Dividimos el contenido del fichero en lineas
		List<String> document = new LinkedList<String>();
		
		//PARA CADA DOCUMENTO, DIVIDIMOS SUS PARTES Y ALMACENAMOS
				for (int i = 0; i < lines.length; ++i) {
					if(lines[i].startsWith(".I")){
						System.out.println("indexing doc: "+lines[i].substring(3));
						document.add(lines[i].substring(3));
					}else if (lines[i].startsWith(".T")){ 
						i++;
						StringBuffer tBuffer = new StringBuffer();
						//System.out.println("OK T");
						while (!(lines[i].startsWith(".A")||lines[i].startsWith(".B")||lines[i].startsWith(".W"))){
							tBuffer.append(lines[i]);
							tBuffer.append("\n");
							i++;
						}
						document.add(tBuffer.toString());
						i--;
					}else if (lines[i].startsWith(".A")){
						i++;
						StringBuffer aBuffer = new StringBuffer();
						//System.out.println("OK A");
						while (!(lines[i].startsWith(".I")||lines[i].startsWith(".B")||lines[i].startsWith(".W"))){
							aBuffer.append(lines[i]);
							aBuffer.append("\n");
							i++;
						}
						i--;
						document.add(aBuffer.toString());
					}else if (lines[i].startsWith(".B")){
						i++;
						StringBuffer bBuffer = new StringBuffer();
						//System.out.println("OK B");
						while (!(lines[i].startsWith(".I")||lines[i].startsWith(".W"))){
							bBuffer.append(lines[i]);
							bBuffer.append("\n");
							i++;
						}
						i--;
						document.add(bBuffer.toString());
					}else if (lines[i].startsWith(".W")){
						i++;
						StringBuffer wBuffer = new StringBuffer();
						//System.out.println("OK W");
						while ((i<lines.length)){
							wBuffer.append(lines[i]);
							wBuffer.append("\n");
							i++;
						}
						document.add(wBuffer.toString());
					}
				}
				//System.out.println(document.size());
				return document;
	}
	
	//SEPARAMOS CADA DOCUMENTO DEL LA COLECCION, PARA LUEGO PARSEAR LAS PARTES DE CADA UNO
	public static List<List<String>> parseString(StringBuffer fileContent) throws Exception{
		/* CONVERTIMOS EL CONTENIDO EN UN STRING */
		//StringBuffer fileContent = readFile(file);

		String text = fileContent.toString();
		String[] lines = text.split("\n"); //Dividimos el contenido del fichero en lineas
		List<List<String>> documents = new LinkedList<List<String>>();
		
		StringBuffer docBuffer = new StringBuffer();
		for(int i=0; i<lines.length; i++){
			if (lines[i].startsWith(".I")){
				docBuffer.append(lines[i]);
				docBuffer.append("\n");
				i++;
				while((i<lines.length)&& (!lines[i].startsWith(".I"))){
					docBuffer.append(lines[i]);
					docBuffer.append("\n");
					i++;
				}
				i--;
				documents.add(parseDocument(docBuffer));
				docBuffer.delete(0, docBuffer.length());
			}
		}
		
		return documents;

	}
	
	
	
}
