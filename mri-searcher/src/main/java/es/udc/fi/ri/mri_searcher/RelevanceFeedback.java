package es.udc.fi.ri.mri_searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.QueryBuilder;


public class RelevanceFeedback {

	private static List<Entry<Term,Double>> orderForMax(Map map){
		Set<Entry<Term,Double>> set = map.entrySet();
		List<Entry<Term,Double>> list = new ArrayList<Entry<Term, Double>>(set);
		
		Collections.sort( list, new Comparator<Map.Entry<Term, Double>>()
        {
            public int compare( Map.Entry<Term, Double> o1, Map.Entry<Term, Double> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
		return list;
		
	}
	
	private static Map<Term,Double> getTerminosQuery(int tq,QuerY query,List<String> fieldsproc, IndexReader reader) throws IOException{
		String[] termsText = query.getText().split("\\s");
		Map<Term, Double> idfTerms = new HashMap<Term, Double>();
		for(int i=0; i<termsText.length; i++){
			for(int j= 0; j<fieldsproc.size(); j++){
				Term termino = new Term(fieldsproc.get(j),termsText[i]);
				int df_t = reader.docFreq(termino);
				TFIDFSimilarity tfidf = new ClassicSimilarity();
				double idf = tfidf.idf(df_t, reader.numDocs());
				idfTerms.put(termino, idf);
			}
		}
		return idfTerms;
	}
	
	public static void bestIdfTerms (int tq, QuerY query,List<String> fieldsproc, IndexReader reader, Analyzer analyzer) throws IOException{
		Map<Term, Double> idfTerms = getTerminosQuery(tq,query, fieldsproc, reader);
		List<Entry<Term,Double>> orderedTerms = orderForMax(idfTerms);
		java.util.Iterator<Entry<Term, Double>> it = orderedTerms.iterator();
		int i=1;
		Entry<Term,Double> entry;

		while( i<=tq ){
			entry=it.next();
			//System.out.println("TERMINO Q: "+entry.getKey()+" VALOR: "+entry.getValue());
			query.addTerm(new TermQuery(entry.getKey()), BooleanClause.Occur.SHOULD);
			i++;
		}		
		
	} 
	
	private static Map<Term,Double> getTerminosDocumentos (Terms vector,String field, IndexReader reader,Map<Term,Double> terms) throws IOException{
		TFIDFSimilarity tfidf = new ClassicSimilarity();
		//Map<Term, Double> tfIdfMap = new HashMap<Term, Double>();
		if(vector!=null){
			TermsEnum termsEnum = null;
			termsEnum = vector.iterator();
			while(termsEnum.next()!=null){
				int df_t = termsEnum.docFreq();
				double idf = tfidf.idf(df_t, reader.numDocs());
				double tf = tfidf.tf(termsEnum.totalTermFreq());
				Term termino = new Term(field,termsEnum.term());
				if(terms.containsKey(termino)){
					if (terms.get(termino)<(tf*idf)){
						terms.put(new Term(field,termsEnum.term()), tf*idf);
					}
				}else{
					terms.put(new Term(field,termsEnum.term()), tf*idf);
				}
			}
		}
		return terms;
	}
	
	private static void ndrDocs( int td, int ndr, QuerY query,List<String> fieldsproc, IndexReader reader) throws IOException{
		List<Integer> relevantes = query.getRelevants();
		Map<Term,Double> terms = new HashMap<Term, Double>();
		for(int i= 0; i<ndr; i++){
			if(i==relevantes.size()){
				break;
			}
			int id = relevantes.get(i);
			Terms termsT = reader.getTermVector(id-1, "T");
			Terms termsW = reader.getTermVector(id-1, "W");
			terms = getTerminosDocumentos(termsT, "T", reader,terms);
			terms = getTerminosDocumentos(termsW, "W", reader,terms);
		}
		List<Entry<Term,Double>> orderedTerms = orderForMax(terms);
		java.util.Iterator<Entry<Term, Double>> it = orderedTerms.iterator();
		int i=1;
		Entry<Term,Double> entry;
		while( i<=td ){
			entry=it.next();
			//System.out.println("TERMINO D: "+entry.getKey()+" VALOR: "+entry.getValue());
			query.addTerm(new TermQuery(entry.getKey()), BooleanClause.Occur.SHOULD);
			i++;
		}		
	}
	
	public static Query rf2 (int ndr,QuerY q,IndexReader reader,List<String> fieldsproc, Analyzer analyzer ) throws IOException, ParseException{
		List<Integer> relevantes = q.getRelevants();
		BooleanClause.Occur[] operator = new BooleanClause.Occur[fieldsproc.size()];
		String [] fields = fieldsproc.toArray(new String[0]);
		for(int i=0;i<fieldsproc.size();i++){
			operator[i]=BooleanClause.Occur.SHOULD;
		}
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(q.getQuery(),BooleanClause.Occur.SHOULD);
		for(int i= 0; i<ndr; i++){
			if(i==relevantes.size()){
				break;
			}
			int id = relevantes.get(i);
			Document doc = reader.document(id-1);
			String title = doc.get("T");
			String queryText = QueryParser.escape(title);
			String [] queryArray = new String[fieldsproc.size()];
			for(int j= 0; j<fieldsproc.size(); j++){
				queryArray[j] = queryText;
			}
			
			Query query = MultiFieldQueryParser.parse(queryArray, fields, operator, analyzer);
			builder.add(query,BooleanClause.Occur.SHOULD );
		}
		return builder.build();
	}
	
	public static void rf1(int tq, int td, int ndr, QuerY query,List<String> fieldsproc, IndexReader reader, Analyzer analyzer ) throws IOException{
		bestIdfTerms(tq, query, fieldsproc, reader, analyzer);
		ndrDocs( td, ndr, query, fieldsproc, reader);
	}
	

}
