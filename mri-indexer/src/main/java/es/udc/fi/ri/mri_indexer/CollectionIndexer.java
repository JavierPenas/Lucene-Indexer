package es.udc.fi.ri.mri_indexer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CollectionIndexer {
	

	public static final FieldType TYPE_STORED = new FieldType();
	static final IndexOptions options = IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
	static{
		TYPE_STORED.setIndexOptions(options);
		TYPE_STORED.setTokenized(true);
		TYPE_STORED.setStored(true);
		TYPE_STORED.setStoreTermVectors(true);
		TYPE_STORED.setStoreTermVectorPositions(true);
		TYPE_STORED.freeze();
	}
	public static final FieldType TYPE_STORED2 = new FieldType();
	static{
		TYPE_STORED2.setIndexOptions(options);
		TYPE_STORED2.setTokenized(false);
		TYPE_STORED2.setStored(true);
		TYPE_STORED2.setStoreTermVectors(true);
		TYPE_STORED2.setStoreTermVectorPositions(true);
		TYPE_STORED2.freeze();
	}
	//INDEXAMOS TODOS LOS CAMPOS PARA CADA DOCUMENTO OBTENIDO
	static void addFields(IndexWriter writer,List<List<String>> documents, File file){
		int i = 1; //PARA POSICION DEL DOC DENTRO DEL ARCHIVO SGM
		for(List<String> document : documents){
			try {
				Document doc = new Document();
				doc.add(new Field("title",document.get(0),TYPE_STORED));
				doc.add(new Field("body",document.get(1),TYPE_STORED));
	  			doc.add(new Field("topics",document.get(2),TYPE_STORED));
			  	doc.add(new Field("dateline",document.get(3),TYPE_STORED));
			  	doc.add(new Field("hostname",InetAddress.getLocalHost().getHostName(),TYPE_STORED2));
			  	doc.add(new Field("thread",Thread.currentThread().getName(),TYPE_STORED2));
			  	doc.add(new Field("PathSgm",file.getPath(),TYPE_STORED2));
			  	doc.add(new Field("seqDocNumber", Integer.toString(i),TYPE_STORED));
			  	i++;
			  	doc.add(new Field("date", Utilities.parseDate(document.get(4)),TYPE_STORED));

			  	writer.addDocument(doc);
			  	
			} catch (UnknownHostException e) {
				System.out.println("Can't resolve hostname");
			} catch (IOException  e2){
				System.out.println("Can't write the document");
			}
		}
	}
	
	//PARSEA Y ENVIA A ADDFIELDS
	public static void indexDocs(IndexWriter writer, File file){

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
    		}else if(Utilities.ValidateName(file.getName())==1){
    			
    			//EN CASO DE QUE SEA UN DOCUMENTO INDIVIDUAL, LO INDEXAMOS
    			//PARA INDEXAR PRIMERO LO ABRIMOS Y CODIFICAMOS
    			//InputStreamReader PASA DE BYTES A CARACTERES SEGUN CODIFICACION	
    			InputStreamReader input = Utilities.openDocument(file);
    			
    			
    			if (input!=null){
    				//BufferedReader LEE UN TEXTO EN FORMATO InputStreamReader
    				//PROVEE FORMA EFICIENTE DE LECTURA POR LINEAS
    				BufferedReader buffer = new BufferedReader(input);
    				//StringBuffer ALMACENA EL CONTENIDO
    				//PARECIDO A STRING PERO SOPORTA THREADS
    				//ES EL TIPO DE ENTRADA DE PARSER REUTERS
    				StringBuffer fileContent = new StringBuffer();
    				String text = null;
    				//PREPARAMOS BUFFER PARA DARSELO AL PARSER
    				try {
						while((text=buffer.readLine()) != null){
							fileContent.append(text).append("\n");
						}
						
					} catch (IOException e) {
						System.out.println("Failure while reading doc");
					}
    				
    				//PARSEAMOS EL CONTENIDO DEL DOCUMENTO
    				//OBTENEMOS UNA LISTA DE LISTAS. UNA LISTA PARA CADA FIELD
    				List<List<String>> documents = Reuters21578Parser.parseString(fileContent);
    				
    				//AÑADIMOS ESTOS FIELDS AL INDICE
    				addFields(writer, documents,file);		
    				
    				System.out.println("File "+file.getPath()+" procesed ..");
    			}
    		}
    	}
	}

	private static void indexer(){
		
	}
	public static void main (String args[]){
		//INSTRUCCIONES DE USO
		String usage = "mri_indexer Usage: "
                 + " [-index INDEX_PATH] [-coll DOCS_PATH] [-openmode create||append||create_or_append ]\n\n"
                 + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                 + "in INDEX_PATH";
		//OPCIONES 
		int best_idfterms = 0;
		int poor_idfterms = 0;
		int best_tfidfterms = 0;
		int poor_tfidfterms = 0;
		int indexar = 0;
		int procesar = 0;
		int reindexar = 0;
		
		//PARAMETROS DE INDEXACION
		String openmode = "create"; //MODO DE INDEXACION, POR DEFECTO CREATE
		String indexPath = null; //PATH DONDE SE CONSTRUIRA INDICE
		//String docsPath = null; //PATH DONDE ESTA LA COLECCION INDEXABLE
		List<String> docsPaths = new ArrayList<String>(); //ARRAY CON DIRECTORIOS A INDEXAR
		List<String> indexes1 = new ArrayList<String>(); //ARRAY CON INDICES
		int indexes2 = 0;
		
		//PARAMETROS PROCESADO DE INDICE
		String indexin = null;
		String field = null;
		String query = null;
		int n = 0;
		
		//PARAMETROS PROCESADO Y RE-CREACION DE INDICE
		String indexout = null;
		String term = null;
		int hilos = 1;
		int mostsimilardoc_body = 0;
		int mostsimilardoc_title = 0;
		int deldocsquery = 0;
		int deldocsterm = 0;
		
		
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
				indexar = 1;
				i++;
			}else if ("-coll".equals(args[i])) {
		        docsPaths.add(args[i+1]);
		        i++;
		    }else if("-colls".equals(args[i])){
		    	while(!args[i+1].startsWith("-")){
		    		docsPaths.add(args[i+1]);
		    		i++;
		    	}
		    	
		    }else if ("-indexes1".equals(args[i])){
		    	while(!args[i+1].startsWith("-")){
		    		indexes1.add(args[i+1]);
		    		indexar = 1;
		    		i++;
		    	}	
		    }else if ("-indexes2".equals(args[i])){
		    	indexPath=args[i+1];
		    	indexar = 1;
		    	indexes2= 1;
		    	i++;
		    }else if ("-indexin".equals(args[i])){
		    	indexin= args[i+1];
		    	procesar =1;
		    	i++;
		    }else if("-best_idfterms".equals(args[i])){
		    	field = args[i+1];
		    	n = Integer.parseInt(args[i+2]);
		    	best_idfterms=1;
		    	i+=2;
		    }else if("-poor_idfterms".equals(args[i])){
		    	field = args[i+1];
		    	n = Integer.parseInt(args[i+2]);
		    	poor_idfterms = 1;
		    	i+=2;
		    }else if("-best_tfidfterms".equals(args[i])){
		    	field = args[i+1];
		    	n = Integer.parseInt(args[i+2]);
		    	best_tfidfterms = 1;
		    	i+=2;
		    }else if("-poor_tfidfterms".equals(args[i])){
		    	field = args[i+1];
		    	n = Integer.parseInt(args[i+2]);
		    	poor_tfidfterms = 1;
		    	i+=2;
		    }else if("-indexout".equals(args[i])){
		    	indexout= args[i+1];
		    	reindexar = 1;
		    	i++;
		    }else if("-deldocsterm".equals(args[i])){
		    	field = args[i+1];
		    	term = args[i+2];
		    	deldocsterm = 1;
		    	i+=2;
		    }else if("-deldocsquery".equals(args[i])){
		    	query = args[i+1];
		    	deldocsquery = 1;
		    	i++;
		    }else if("-mostsimilardoc_title".equals(args[i])){
		    	hilos = Integer.parseInt(args[i+1]);
		    	mostsimilardoc_title = 1;
		    	i++;
		    }else if("-mostsimilardoc_body".equals(args[i])){
		    	n = Integer.parseInt(args[i+1]);
		    	hilos = Integer.parseInt(args[i+2]);
		    	mostsimilardoc_body = 1;
		    	i+=2;
		    }
			
		}
		
		if(indexar==1){
			if (indexPath==null){
	    	    indexes1(indexes1,docsPaths,openmode);
	    	}else if(indexes2==0){
	    		index(indexPath,docsPaths,openmode);
	    	}else if(indexes2==1){
	    		indexes2(indexPath,docsPaths);
	    	}
		}
		
		if(procesar==1){
			IndexProcesser processer = new IndexProcesser(indexin);
			if(best_idfterms==1){
				processer.bestIdfTerms(field, n);
			}
			if(poor_idfterms==1){
				processer.poorIdfTerms(field, n);
			}
			if(best_tfidfterms==1){
				processer.bestTfIdfTerms(field, n);
			}
			if(poor_tfidfterms==1){
				processer.poorTfIdfTerms(field, n);
			}
		}
		
		if(reindexar==1){
			IndexConstructor constructor = new IndexConstructor(indexout, indexin);
			if(deldocsterm==1){
				constructor.deldocsterm(field, term);
			}
			if(deldocsquery==1){
				constructor.deldocsquery(query);
			}
			if(mostsimilardoc_title==1){
				
			}
			if(mostsimilardoc_body==1){
				
			}
		}
		
		

}
	
private static void index(String indexPath,List<String> docsPaths,String openmode){
	try{
		Directory dir;
		dir = FSDirectory.open(Paths.get(indexPath));
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

		
		for(String docPath: docsPaths){
			File docsDir = new File(docPath);
			if(!docsDir.exists()||!docsDir.canRead()){
				System.out.println("Document directory "+docsDir.getAbsolutePath()+" does not exist or is not readable");
				System.exit(1);
			}
			indexDocs(writer, docsDir);
		}
		writer.close();
		
	 }catch (IOException e){
		 
	 }

	
}
private static void indexes1(List<String> indexes1,List<String> docsPaths, String openmode){
	 
	final int numCores = Runtime.getRuntime().availableProcessors();
	final ExecutorService executor = Executors.newFixedThreadPool(docsPaths.size());

	
	for(int i=1; i<indexes1.size(); i++){
		String indexP = indexes1.get(i);
		String docP = docsPaths.get(i-1);
		final Runnable worker = new WorkerThread(indexP,openmode,docP);
		executor.execute(worker);
	}
	
    try{
    	executor.shutdown();
    	executor.awaitTermination(1, TimeUnit.HOURS);
    }catch(final InterruptedException e){
    	e.printStackTrace();
    	System.exit(-2);
    }
//ACABAN DE EJECUTARSE TODOS LOS THREADS
    
	List<Directory> directories = new ArrayList<Directory>();
	for(int i=1; i<indexes1.size(); i++){
		Directory directory = null;
		String path = indexes1.get(i);
		try{
			directory = FSDirectory.open(Paths.get(path));
			directories.add(directory);
		}catch (CorruptIndexException e1){
			System.out.println("Graceful message: exception"+ e1);
			e1.printStackTrace();
		}catch (IOException e1){
			System.out.println("Graceful message: exception"+ e1);
			e1.printStackTrace();
		}
	}
	//EN DIRECTORIES TENEMOS ARRAY CON LOS DIRECTORIOS DE LOS INDICES
   	Directory[] dirs= directories.toArray(new Directory[directories.size()]);
   	
	try{
		Directory dir;
		dir = FSDirectory.open(Paths.get(indexes1.get(0)));
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
		writer.addIndexes(dirs);
		writer.close();
	 }catch (IOException e){
		 e.printStackTrace();
	 }

}

private static void indexes2 (String indexPath,List<String> docsPaths){
	final int numCores = Runtime.getRuntime().availableProcessors();
	final ExecutorService executor = Executors.newFixedThreadPool(numCores);

	try{
		Directory dir;
		dir = FSDirectory.open(Paths.get(indexPath));
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);		
		IndexWriter writer = new IndexWriter(dir, iwc);

		for(int i=0; i<docsPaths.size(); i++){
			String docP = docsPaths.get(i);
			final Runnable worker = new WorkerThread2(docP,writer);
			executor.execute(worker);
		}
		
	    try{
	    	executor.shutdown();
	    	executor.awaitTermination(1, TimeUnit.HOURS);
	    }catch(final InterruptedException e){
	    	e.printStackTrace();
	    	System.exit(-2);
	    }
	    //CUANDO TERMINAN TODOS LOS THREADS CERRAMOS WRITER
		writer.close();
	 }catch (IOException e){
		 e.printStackTrace();
	 }	
}

}

