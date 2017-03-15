package es.udc.fi.ri.mri_indexer;

import java.io.File;
import java.io.IOException;
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

public class WorkerThread implements Runnable {

	private final String indexPath;
	private final String openmode;
	private final String docsPath;
	//private final IndexWriter writer;

	public WorkerThread(final String indexPath, final String openmode, final String docsPath) {
		this.indexPath = indexPath;
		this.openmode = openmode;
		this.docsPath = docsPath;
		//this.writer = writer;
	}

	public void run() {
		 //PREPARAMOS LA CONFIGURACION DEL INDEXADOR
	    //ANALYZER; WRITER Y DIRECTORIO
	    Date start = new Date();
	    File docsDir = new File(docsPath);
		if(!docsDir.exists() || !docsDir.canRead()){
				  System.out.println("Document directory '" +docsDir.getAbsolutePath()
				  + "' does not exist or is not readable, please check the path");
				  System.exit(1);	    		
		}
		try{
			 Directory dir= FSDirectory.open(Paths.get(indexPath));
	
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
			CollectionIndexer.indexDocs(writer,docsDir);  //EN INDEX DOCS DEBEMOS MIRAR SI EL DOCUMENTO PASADO ES UN ARCHIVO FINAL O UN DIRECTORIO
											 //SI ES ARCHIVO FINAL INDEXAMOS, SINO SEGUIMOS BAJANDO 
			writer.close();
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");
		}catch(IOException e){
			
		}
	}

}
