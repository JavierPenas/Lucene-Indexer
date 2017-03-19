package es.udc.fi.ri.mri_indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;

public class BodyIndexConstructorThread implements Runnable {

	private final String indexPath;
	private final IndexWriter writer;
	private final IndexReader reader;
	private final IndexSearcher searcher;
	private final int docIni;
	private final int docFin;
	private final int n;
	
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
	
	public BodyIndexConstructorThread(final String indexPath, final IndexWriter writer, final IndexReader reader, final IndexSearcher searcher,final int docIni,final int docFin, final int n) {
		this.indexPath = indexPath;
		this.writer = writer;
		this.reader = reader;
		this.searcher = searcher;
		this.docFin = docFin;
		this.docIni = docIni;
		this.n = n;
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
	
	private static List<TfIdfObject> orderTdIdfMax(List<TfIdfObject> list){
		
		Collections.sort( list, new Comparator<TfIdfObject>()
        {
			
            public int compare( TfIdfObject o1, TfIdfObject o2 )
            {
            	return (o2.getTfIdf().compareTo(o1.getTfIdf()));
            }
            
        } );
		return list;
	}
	
	
	public void run() {
		Date start = new Date();
		int N = reader.numDocs();
	    try{
			for(int id = docIni; id<=docFin; id++){
				
				Document doc =  reader.document(id);
				TFIDFSimilarity tdfidf = new ClassicSimilarity();
				
				Terms vector = reader.getTermVector(id, "body");
				//Si es == null es que el body está vacío
				if (vector != null){
					
					TermsEnum termsEnum = null;
					termsEnum = vector.iterator();
					List<TfIdfObject> tfIdfList = new ArrayList<TfIdfObject>();
					
		        	while(termsEnum.next()!=null){
		        		
						int df_t = termsEnum.docFreq(); //NUMERO DE DOCUMENTOS DONDE APARECE EL TERMINO
						//double idf = Math.log10(N/df_t); //CALCULO IDF DEL TERMINO
						double idf = tdfidf.idf(df_t, N);
						double tf = termsEnum.totalTermFreq();
						//double tf = tdfidf.tf(termsEnum.totalTermFreq());
						if(termsEnum.totalTermFreq()==0){
							tf = 0;
						}else{
							tf = 1 + Math.log10(termsEnum.totalTermFreq());
						}
						tfIdfList.add(new TfIdfObject(tf, df_t, idf, id, termsEnum.term().utf8ToString())); 
		        	}
		        	
		        	//mejores term tfidf para el documento
		    		List<TfIdfObject> orderedlist = orderTdIdfMax(tfIdfList);
		    		String squery = "";
		    		for(int i= 0; i<n; i++){
		    			//Si no hay términos suficientes, break y nos quedamos con los que hay.
		    			if (i >= orderedlist.size())
		    				break;
		    				
		    			squery = squery + orderedlist.get(i).getTerm() + " ";
		    		}
				
					Query query = createQuery(squery);
					TopDocs topDocs = searcher.search(query,2);
					ScoreDoc [] hits = topDocs.scoreDocs;
					if(hits.length>1){
						ScoreDoc score = hits[1];
						Document simDoc = searcher.doc(score.doc);
						//System.out.println("Similar docs to "+id+" : "+score.doc+" with path: "+simDoc.getField("PathSgm").stringValue());
						doc.add(new Field("SimQuery",query.toString(),TYPE_STORED));;
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
