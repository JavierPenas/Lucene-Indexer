package es.udc.fi.ri.mri_indexer;

import java.io.File;
import java.io.IOException;
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
	    	//indexDocs(writer,docDir);  //EN INDEX DOCS DEBEMOS MIRAR SI EL DOCUMENTO PASADO ES UN ARCHIVO FINAL O UN DIRECTORIO
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
