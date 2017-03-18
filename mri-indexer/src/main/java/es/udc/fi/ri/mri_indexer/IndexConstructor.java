package es.udc.fi.ri.mri_indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexConstructor {
	
	private final String indexout;
	private final String indexin;
	
	public IndexConstructor(String indexout, String indexin){
		this.indexin = indexin;
		this.indexout = indexout;
	}
	
	private IndexWriter createWriter(String index){
		try{
			Directory dir;
			dir = FSDirectory.open(Paths.get(index));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			IndexWriter writer = new IndexWriter(dir, iwc);
			return writer;
		 }catch (IOException e){
			 
		 }
		return null;
	}
	private IndexReader createReader(){
		try{
			Directory dir = null;
			DirectoryReader indexReader = null;
		
			dir = FSDirectory.open(Paths.get(indexin));
			indexReader = DirectoryReader.open(dir);
			return indexReader;
		}catch(IOException e){
			
		}
		return null;
	}
	
	
	public void deldocsterm(String field, String termino){
		IndexWriter writer = createWriter(indexin);
		try{
			Term term = new Term(field, termino);
			System.out.println("delete term "+termino+" "+field);
			writer.deleteDocuments(term);
			writer.forceMergeDeletes();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			writer.commit();
			writer.close();
		}catch (CorruptIndexException e) {
			System.out.println("Graceful message: exception "+e);
			e.printStackTrace();
		}catch (IOException e) {
			System.out.println("Graceful message: exception "+e);
			e.printStackTrace();
		}
	}
	
	public void deldocsquery(String squery){
		IndexReader reader = createReader();
		IndexWriter writer = createWriter(indexin);
		IndexSearcher searcher = new IndexSearcher(reader);
		try {
			Fields fields = MultiFields.getFields(reader);
			//String [] arrayFields = new String[fields.size()];
			Iterator<String> it = fields.iterator();
			while(it.hasNext()){
				//arrayFields[i] = it.next();
				QueryParser parser = new QueryParser(it.next(), new StandardAnalyzer());
				Query query = parser.parse(squery);
				writer.deleteDocuments(query);
				writer.forceMergeDeletes();
			}
			writer.commit();
			writer.close();
			}catch (CorruptIndexException e) {
				System.out.println("Graceful message: exception "+e);
				e.printStackTrace();
			}catch (IOException e) {
				System.out.println("Graceful message: exception "+e);
				e.printStackTrace();
			}catch(ParseException e){
				System.out.println("Graceful message: exception "+e);
				e.printStackTrace();
			}
			
	}
	
	
	public void mostsimilardocTitle(int hilos){
		IndexReader reader = createReader();
		Directory dir;
		try {
			dir = FSDirectory.open(Paths.get(indexout));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE);		
			IndexWriter writer;
			writer = new IndexWriter(dir, iwc);
	
			IndexSearcher searcher = new IndexSearcher(reader);
			
			int [] pDocs = new int[hilos];
			int totalDocs = reader.numDocs();
			int docsPorHilo = Math.floorDiv(totalDocs,hilos); //Documentos a procesar por cada hilo
			
			for(int hilo=0; hilo<hilos; hilo++){
				pDocs[hilo]=docsPorHilo;
			}
			if((totalDocs%hilos)!=0){
				pDocs[0]+=1;
			}
			
	
			final int numCores = Runtime.getRuntime().availableProcessors();
			final ExecutorService executor = Executors.newFixedThreadPool(hilos);
			int docIni = 0;
			int docFin = 0;
			for(int hilo=0; hilo<hilos; hilo++){
			
				if(hilo ==0){
						docIni = 0;
						docFin = pDocs[0]-1;
				}else{
						docIni = docIni+pDocs[hilo-1];
						docFin = docFin+pDocs[hilo];
				}
				System.out.println("THREAD "+hilo+" INI "+docIni+" FIN "+docFin);
				final Runnable worker = new TitleIndexConstructorThread(indexin, writer, reader, searcher, docIni, docFin);
				executor.execute(worker);
			}
			executor.shutdown();
		    executor.awaitTermination(1, TimeUnit.HOURS);

		    writer.close();
		}catch(IOException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		

	}
	
	
	public void mostsimilardocBody(int n,int hilos){
		IndexReader reader = createReader();
		Directory dir;
		try {
			dir = FSDirectory.open(Paths.get(indexout));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE);		
			IndexWriter writer;
			writer = new IndexWriter(dir, iwc);
	
			IndexSearcher searcher = new IndexSearcher(reader);
			
			int [] pDocs = new int[hilos];
			int totalDocs = reader.numDocs();
			int docsPorHilo = Math.floorDiv(totalDocs,hilos); //Documentos a procesar por cada hilo
			
			for(int hilo=0; hilo<hilos; hilo++){
				pDocs[hilo]=docsPorHilo;
			}
			if((totalDocs%hilos)!=0){
				pDocs[0]+=1;
			}
			
			final ExecutorService executor = Executors.newFixedThreadPool(hilos);
			int docIni = 0;
			int docFin = 0;
			for(int hilo=0; hilo<hilos; hilo++){
			
				if(hilo ==0){
						docIni = 0;
						docFin = pDocs[0]-1;
				}else{
						docIni = docIni+pDocs[hilo-1];
						docFin = docFin+pDocs[hilo];
				}
				System.out.println("THREAD "+hilo+" INI "+docIni+" FIN "+docFin);
				final Runnable worker = new BodyIndexConstructorThread(indexin, writer, reader, searcher, docIni, docFin, n);
				executor.execute(worker);
			}
			executor.shutdown();
		    executor.awaitTermination(1, TimeUnit.HOURS);

		    writer.close();
		}catch(IOException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		

	}

	
	
	
}
