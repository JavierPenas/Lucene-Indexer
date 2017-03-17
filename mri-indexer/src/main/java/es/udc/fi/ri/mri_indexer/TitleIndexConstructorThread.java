package es.udc.fi.ri.mri_indexer;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class TitleIndexConstructorThread implements Runnable {

	private final String indexPath;
	private final IndexWriter writer;
	private final IndexReader reader;
	private final IndexSearcher searcher;
	private final int docIni;
	private final int docFin;
	
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
	
	public TitleIndexConstructorThread(final String indexPath, final IndexWriter writer, final IndexReader reader, final IndexSearcher searcher,final int docIni,final int docFin) {
		this.indexPath = indexPath;
		this.writer = writer;
		this.reader = reader;
		this.searcher = searcher;
		this.docFin = docFin;
		this.docIni = docIni;
	}
	
	private Query createQuery(String text){
		String querytext = QueryParser.escape(text);
		try {
			
			Query query = MultiFieldQueryParser.parse(new String []{querytext,querytext}, new String []{"title","body"},new StandardAnalyzer());
			
			return query;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	@Override
	public void run() {
		Date start = new Date();
	    try{
			for(int id= docIni; id<=docFin; id++){
				Document doc =  reader.document(id);
				if(!doc.get("title").equals("")){
					Query query = createQuery(doc.get("title"));
					TopDocs topDocs =  searcher.search(query,2);
					ScoreDoc [] hits = topDocs.scoreDocs;
					if(hits.length>1){
						ScoreDoc score = hits[1];
						Document simDoc = searcher.doc(score.doc);
						//System.out.println("Similar docs to "+id+" : "+score.doc+" with path: "+simDoc.getField("PathSgm").stringValue());
						doc.add(new Field("SimPathSgm",simDoc.getField("PathSgm").stringValue(),TYPE_STORED));
						doc.add(new Field("SimTitle",simDoc.getField("title").stringValue(),TYPE_STORED));
						doc.add(new Field("SimBody",simDoc.getField("body").stringValue(),TYPE_STORED));
						writer.addDocument(doc);
					}else{
						writer.addDocument(doc);
					}
				}else{
					writer.addDocument(doc);
				}
			}
	    }catch(IOException e){
	    	
	    }
		
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");
		
	}

}
