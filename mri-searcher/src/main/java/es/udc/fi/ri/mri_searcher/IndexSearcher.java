package es.udc.fi.ri.mri_searcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
		Map<String, Float> indexModel = new HashMap<String, Float>(); //MODELO INDEXACION
		List<String> docsPaths = new ArrayList<String>(); //ARRAY CON DIRECTORIOS A INDEXAR
		
		//VARIABLES BUSQUEDA, RELEVANCE FEEDBACK, y PS RELEVANCE FEEDBACK
		String indexin; 
		List<String> fieldsproc = new ArrayList<String>();
		Map<String, Float> searchModel = new HashMap<String, Float>(); //MODELO BUSQUEDA
		int cut = 0;
		int top = 0;
		
		int ini = -1;
		int fin = -1;
		
		
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
			}else if("-indexin".equals(args[i])){
				indexin = args[i+1];
				i++;
			}else if ("-coll".equals(args[i])) {
				docsPaths.add(args[i+1]);
		        i++;
		    }else if("-search".equals(args[i])){
		    	if ((args[i+1]).equals("default")||(args[i+1]).equals("jm")||(args[i+1]).equals("dir")){
		    		if(indexModel.get(args[i+1])!=null){
		    			searchModel.put(args[i+1], indexModel.get(args[i+1]));
		    			i++;
		    		}else{
		    			System.out.println("search model must be the same in indexing and searching");
						System.out.println("exiting");
						System.exit(1);
		    		}
		    	}
		    }else if("-indexingmodel".equals(args[i])){
		    	if ((args[i+1]).equals("jm")||(args[i+1]).equals("dir")){
		    		indexModel.put(args[i+1], Float.parseFloat(args[i+2]));
					i+=2;
				} else if ((args[i+1]).equals("default")){
					indexModel.put(args[i+1], (float) 0.0);
					i++;
				}else{
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
		    	Pattern p = Pattern.compile("\\d++-\\d++");
		    	Pattern p2 = Pattern.compile("\\d++");
		    	if("all".equals(args[i+1])){
		    		//Es por defecto, no hacemos nada
		    	}else if(p.matcher(args[i+1]).matches()){
		    		ini = Integer.parseInt(args[i+1].substring(0, args[i+1].indexOf("-")));
		    		fin = Integer.parseInt(args[i+1].substring(args[i+1].indexOf("-")+1));
		    	}else if(p2.matcher(args[i+1]).matches()){
		    		ini = Integer.parseInt(args[i+1]);
		    	}else{
		    		System.out.println("only valid all|int1|int1-int2 for -queries parameter");
					System.out.println("exiting");
					System.exit(1);
		    	}
		    	i++;
		    }else if("-fieldsproc".equals(args[i])){
		    	i++;
		    	while(args[i].equals("T")||args[i].equals("W")){
		    		fieldsproc.add(args[i+1]);
		    		i++;
		    	}
		    	i--;
		    }else if("-fieldsvisual".equals(args[i])){
		    	
		    }
	}
		
	//INDEXACION DE DOCUMENTOS
		 Date start = new Date();
		 System.out.println("Indexing ...");
		 CollectionIndexer.index(indexModel,indexPath,docsPaths,openmode);
		 Date end = new Date();
		 System.out.println("Total indexing time : "+ (end.getTime() - start.getTime()) + " milliseconds");
		
		//VARIABLES DE ARCHIVO QUERIES Y QRELS
		String queriesPath = (docsPaths.get(0)+"\\cran.qry");
		String qrelsPath = (docsPaths.get(0)+"\\cranqrel");
		try {
			CranQueryParser.parseDocument(queriesPath, qrelsPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}


}