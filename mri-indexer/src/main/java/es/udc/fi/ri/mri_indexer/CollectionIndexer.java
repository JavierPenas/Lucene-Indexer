package es.udc.fi.ri.mri_indexer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CollectionIndexer {
/*	private int ValidateName(String s){
		String s1 = s.substring(0,5);
		int s2 = Integer.parseInt(s.substring(6,8));
		String s3 = s.substring(s.lastIndexOf(".")+1);
		
		if ((s1.equals("reut2") & (s3.equals(".sgm") & (s2<1000) & (s2>=0)){
			return 1;
		}else {
			return 0;
		}
	}
*/

	private InputStreamReader openDocument(File file){
		FileInputStream input = null;
		//FALTA VALIDAR NOMBRE
		try {
			input = new FileInputStream(file);
			InputStreamReader in = new InputStreamReader(input, StandardCharsets.UTF_8);
			return in;
		} catch (FileNotFoundException e) {
			return null;
		}
		
		
	}
	
	private void indexDocs(IndexWriter writer, File file){

    	if (file.canRead()){ //SI EL ARCHIVO NO SE PUEDE LEER, NO PODEMOS INDEXARLO
    		//SI SE TRATA DE UN DIRECTORIO HAY QUE RECORRERLO
    		if(file.isDirectory()){
    			File documents[] = file.listFiles();
    			if(documents != null){
    				for(int i= 0; i<documents.length; i++){
    					//LLAMAMOS DE NUEVO A INDEXDOCS CON EL CONTENIDO DEL DIRECTORIO
    					indexDocs(writer, documents[i]);
    				}
    			}
    		}else{
    			//EN CASO DE QUE SEA UN DOCUMENTO INDIVIDUAL, LO INDEXAMOS
    			//PARA INDEXAR PRIMERO LO ABRIMOS Y CODIFICAMOS
    			//InputStreamReader pasa de bytes a caracteres, segun codificacion indicada
    			InputStreamReader input = openDocument(file);
    			
    			
    			if (input!=null){
    				//BufferedReader lee texto en un inputStream de caracteres
    				//y provee una forma eficiente de leerlos por lineas
    				BufferedReader buffer = new BufferedReader(input);
    				//StringBuffer es para ir almacenando el contenido
    				//Funciona parecido a String pero soporta threads
    				//Es lo que necesita como entrada el parser de Reuters
    				StringBuffer fileContent = new StringBuffer();
    				String text = null;
    				try {
						while((text=buffer.readLine()) != null){
							fileContent.append(text).append("\n");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    					
    			}
    			
    			
    		}
    		
    		
    		
    		
    	}
	}
	
	public static void main (String args[]){
		//INSTRUCCIONES DE USO
		String usage = "mri_indexer Usage: "
                 + " [-index INDEX_PATH] [-coll DOCS_PATH] [-openmode create||append||create_or_append ]\n\n"
                 + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                 + "in INDEX_PATH";
		 
		//PARAMETROS DE INDEXACION
		String openmode = "create"; //MODO DE INDEXACION, POR DEFECTO CREATE
		String indexPath = "index"; //PATH DONDE SE CONSTRUIRA INDICE
		String docsPath = null; //PATH DONDE ESTA LA COLECCION INDEXABLE
		
		//REPASAMOS TODOS LOS PARAMETROS DE ENTRADA PARA RECONOCER LAS OPCIONES
		for(int i=0;i<args.length;i++) {
			if("-openmode".equals(args[i])){
				if ((args[i+1]).equals("create")||(args[i+1]).equals("append")||
						(args[i+1]).equals("create_or_append")){
					openmode = args[i+1];
					i++;
				} else {
					//SI NO COINCIDE CON NINGUN OPENMODE ERROR
					System.out.println("openmode is create, append or create_or_append");
					System.out.println("exiting");
					System.exit(1);
				}	
			}else if ("-index".equals(args[i])){
				indexPath = args[i+1];
				i++;
			}else if ("-coll".equals(args[i])) {
		        docsPath = args[i+1];
		        i++;
		    }
		}
		
		//ES OBLIGATORIO PROPORCIONAR EL DIRECTORIO DE LA COLECCION
	    if (docsPath == null) {
	        System.err.println("Usage: " + usage);
	        System.exit(1);
	    }
	    
	    //EL DIRECTORIO DE LA COLECCION DEBE SER LEIBLE, SINO TENDREMOS ERRORES MAS ADELANTE
	    final Path docDir = Paths.get(docsPath);
	    if (!Files.isReadable(docDir)) {
	      System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
	      System.exit(1);
	    }
	    
	    //PREPARAMOS LA CONFIGURACION DEL INDEXADOR
	    //ANALYZER; WRITER Y DIRECTORIO
	    Date start = new Date();
	    try{
	    	System.out.println("Indexing to directory "+indexPath+" ...");
	    	
	    	Directory dir = FSDirectory.open(Paths.get(indexPath));
	    	Analyzer analyzer = new StandardAnalyzer();
	    	IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	    	
	    	if (openmode.equals("create")){
	    		iwc.setOpenMode(OpenMode.CREATE);
	    	}else if(openmode.equals("append")){
	    		iwc.setOpenMode(OpenMode.APPEND);
	    	}else if(openmode.equals("create_or_append")){
	    		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	    	}
	    	
	    	IndexWriter writer = new IndexWriter(dir, iwc);
	    	
	    	File docsDir = new File(docsPath);
	    	if(!docsDir.exists() || !docsDir.canRead()){
				   System.out.println("Document directory '" +docsDir.getAbsolutePath()
				   + "' does not exist or is not readable, please check the path");
				   System.exit(1);	    		
	    	}	
	    	//indexDocs(writer,docsDir);  //EN INDEX DOCS DEBEMOS MIRAR SI EL DOCUMENTO PASADO ES UN ARCHIVO FINAL O UN DIRECTORIO
	    								 //SI ES ARCHIVO FINAL INDEXAMOS, SINO SEGUIMOS BAJANDO 
	    	
	    	//CERRAMOS EL INDEXWRITER Y IMPRIMIMOS EL TIEMPO QUE HEMOS TARDADO EN INDEXAR
	    	writer.close();
	    	Date end = new Date();
	    	System.out.println(end.getTime() - start.getTime() + " total milliseconds");
	    	
	    }catch (IOException e) {
	        System.out.println(" caught a " + e.getClass() +
	        	       "\n with message: " + e.getMessage());
	    }
	    
	    
	    
	    
	    
	}
}
