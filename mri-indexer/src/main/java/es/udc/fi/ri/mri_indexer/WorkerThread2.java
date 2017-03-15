package es.udc.fi.ri.mri_indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class WorkerThread2  implements Runnable {

	//private final String indexPath;
	//private final String openmode;
	private final String docsPath;
	private final IndexWriter writer;

	public WorkerThread2( final String docsPath,final IndexWriter writer) {
		//this.indexPath = indexPath;
		//this.openmode = openmode;
		this.docsPath = docsPath;
		this.writer = writer;
	}

	public void run() {

	    Date start = new Date();
	    File docsDir = new File(docsPath);
		if(!docsDir.exists() || !docsDir.canRead()){
				  System.out.println("Document directory '" +docsDir.getAbsolutePath()
				  + "' does not exist or is not readable, please check the path");
				  System.exit(1);	    		
		}
		CollectionIndexer.indexDocs(writer,docsDir);  //EN INDEX DOCS DEBEMOS MIRAR SI EL DOCUMENTO PASADO ES UN ARCHIVO FINAL O UN DIRECTORIO

		Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");

	}

}
