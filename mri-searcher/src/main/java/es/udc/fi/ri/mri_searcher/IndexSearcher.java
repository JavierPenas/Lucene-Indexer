package es.udc.fi.ri.mri_searcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IndexSearcher {
	
	
	public static void main (String args[]){
		//INSTRUCCIONES DE USO
		String usage = "mri_searcher Usage: "
                 + " [-index INDEX_PATH] [-coll DOC_PATH] [-openmode create||append||create_or_append ] [-indexingmodel default||jm lmbda||dir mu]\n\n"
                 + "This indexes the documents in DOC_PATH, creating a Lucene index"
                 + "in INDEX_PATH following the indexing model specified";
		
		//VARIABLES DE INDEXACION
		String openmode = "create"; //OPENMODE
		String indexPath = null; //PATH DONDE SE CONSTRUIRA INDICE
		List<String> docsPaths = new ArrayList<String>(); //ARRAY CON DIRECTORIOS A INDEXAR
		
		//VARIABLES BUSQUEDA, RELEVANCE FEEDBACK, y PS RELEVANCE FEEDBACK
		String indexin; 
		String search;
		int cut = 0;
		int top = 0;
		
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
				docsPaths.add(args[i+1]);
		        i++;
		    }else if("indexingmodel".equals(args[i])){
		    	//NOT IMPLEMENTED YET
		    }else if("-search".equals(args[i])){
		    	if ((args[i+1]).equals("default")||(args[i+1]).equals("jm")||
						(args[i+1]).equals("dir")){
					search = args[i+1];
					i++;
				} else {
					//SI NO COINCIDE CON NINGUN OPENMODE ERROR
					System.out.println("openmode is default, jm or dir");
					System.out.println("exiting");
					System.exit(1);
				}	
		    }else if("-cut".equals(args[i])){
		    	cut= Integer.parseInt(args[i+1]);
		    	i++;
		    }else if("-top".equals(args[i])){
		    	top= Integer.parseInt(args[i+1]);
		    	i++;
		    }else if("-queries".equals(args[i])){
		    	//IMPLEMENTAR OPCIONES DISPONIBLES
		    }
	}
		
	//INDEXACION DE DOCUMENTOS
		 Date start = new Date();
		 System.out.println("Indexing ...");
		 CollectionIndexer.index(indexPath,docsPaths,openmode);
		 Date end = new Date();
		 System.out.println("Total indexing time : "+ (end.getTime() - start.getTime()) + " milliseconds");
		
	
}
	
}
