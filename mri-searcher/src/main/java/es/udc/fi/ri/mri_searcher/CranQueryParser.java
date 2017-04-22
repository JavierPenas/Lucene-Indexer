package es.udc.fi.ri.mri_searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class CranQueryParser {
	//ABRE EL DOCUMENTO QUE CONTIENE LAS QUERIES
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
	
	private static List<List<Integer>> parseQrels(File qRelsFile) throws Exception{
		
		StringBuffer qRelsContent = readFile(qRelsFile); //LEEMOS EL FICHERO
		
		String text = qRelsContent.toString();
		String[] lines = text.split("\n"); //Dividimos el contenido del fichero en lineas
		
		List<List<Integer>> AllrelevantDocs = new LinkedList<List<Integer>>(); //LISTA CON LISTAS DE RELEVANTES PARA CADA QUERY
		List<Integer> relevantDocs = new LinkedList<Integer>(); //LISTA CON RELEVANTES PARA UNA QUERY CONCRETA
		
		int beforeId = 1; //SE INICIALIZA A ID 1
		for (int i = 0; i < lines.length; ++i) {
				String[] linea = lines[i].split(" ");
				int id = Integer.parseInt(linea[0]);
				int rel = Integer.parseInt(linea[1]);
				
				if(id == beforeId){
					relevantDocs.add(rel);
				}else{
				
					AllrelevantDocs.add(relevantDocs);
					relevantDocs = new LinkedList<Integer>();
					beforeId = id;
					relevantDocs.add(rel);
				}
		}
		AllrelevantDocs.add(relevantDocs); //Debemos añadir el ultimo 
		return AllrelevantDocs;
	}
	
	public static List<Query> parseDocument(String queriesPath, String qrelsPath) throws Exception{
	
		File queriesDocument = new File(queriesPath); //ABRIMOS ARCHIVO DE QUERIES
		File qrelsDocument = new File(qrelsPath); //ABRIMOS ARCHIVO CON RELEVANCIAS
		
		List<List<Integer>> allRelevantDocs = parseQrels(qrelsDocument); //PARSEAMOS RELEVANCIAS
		StringBuffer documentContent = readFile(queriesDocument); //LEEMOS ARCHIVO QUERIES
		String text = documentContent.toString();
		String[] lines = text.split("\n"); //Dividimos el contenido del fichero en lineas
		
		List<Query> queries = new LinkedList<Query>(); //LISTA CON TODAS LAS QUERIES PROCESADAS
		Query query = null; //QUERY INDEPENDIENTE
		
		//PARA CADA QUERY, DIVIDIMOS SUS PARTES Y ALMACENAMOS
				for (int i = 0; i < lines.length; ++i) {
					if(lines[i].startsWith(".I")){
						query = new Query();
						query.setId(Integer.parseInt(lines[i].substring(3)));
					}else if(lines[i].startsWith(".W")){
						i++;
						StringBuffer wBuffer = new StringBuffer();
						while((i<lines.length)&&(!lines[i].startsWith(".I"))){
							wBuffer.append(lines[i]);
							i++;
						}
						query.setText(wBuffer.toString());
						if(allRelevantDocs.size()>=query.getId()){
							query.setRelevants(allRelevantDocs.get(query.getId()-1));
						}
						queries.add(query);
						i--;
					}
				}
				
			/*	for(Query q: queries){
					System.out.println(q.getId());
					if(q.getRelevants()!=null){
						for(int i: q.getRelevants()){
							System.out.print(i+" ");
						}
						System.out.println(" ");
					}
				}*/
		return queries;
	}
	
	
}
