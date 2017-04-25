package es.udc.fi.ri.mri_searcher;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

public class Metrics {

	public static void p10 (ScoreDoc[] hits, QuerY query, IndexSearcher searcher) throws IOException{
		float relevants = 0;
		int i = 0;
		while(i<10 && i<hits.length){
			ScoreDoc score = hits[i];
			Document doc = searcher.doc(score.doc);
			int idDoc = Integer.parseInt(doc.get("I"));
			//System.out.println(score.score+" "+score.doc );
			if (!(query.getRelevants()==null)&&query.getRelevants().contains(idDoc)){
				relevants++;
			}
			i++;
		}		
		System.out.println("P@10: "+relevants/10);		
	}
	
	public static void p20 (ScoreDoc[] hits, QuerY query, IndexSearcher searcher) throws IOException{
		float relevants = 0;
		int i = 0;
		while(i<20 && i<hits.length){
			ScoreDoc score = hits[i];
			Document doc = searcher.doc(score.doc);
			int idDoc = Integer.parseInt(doc.get("I"));
			//System.out.println(score.score+" "+score.doc );
			if (!(query.getRelevants()==null)&&query.getRelevants().contains(idDoc)){
				relevants++;
			}
			i++;
		}		
		System.out.println("P@20: "+relevants/20);		
	}
	
	public static void recall10 (ScoreDoc[] hits, QuerY query, IndexSearcher searcher) throws IOException{
		float relevants = 0;
		int i = 0;
		while(i<10 && i<hits.length){
			ScoreDoc score = hits[i];
			Document doc = searcher.doc(score.doc);
			int idDoc = Integer.parseInt(doc.get("I"));
			//System.out.println(score.score+" "+score.doc );
			if (!(query.getRelevants()==null)&&query.getRelevants().contains(idDoc)){
				relevants++;
			}
			i++;
		}
		System.out.println("Recall@10: "+relevants/query.getRelevants().size());		
	}
	
	public static void recall20 (ScoreDoc[] hits, QuerY query, IndexSearcher searcher) throws IOException{
		float relevants = 0;
		int i = 0;
		while(i<20 && i<hits.length){
			ScoreDoc score = hits[i];
			Document doc = searcher.doc(score.doc);
			int idDoc = Integer.parseInt(doc.get("I"));
			//System.out.println(score.score+" "+score.doc );
			if (!(query.getRelevants()==null)&&query.getRelevants().contains(idDoc)){
				relevants++;
			}
			i++;
		}
		System.out.println("Recall@20: "+relevants/query.getRelevants().size());		
	}
	
	public static float aveP(ScoreDoc[] hits, QuerY query, IndexSearcher searcher) throws IOException{
		float relevants = 0;
		float suma = 0;
		int i = 1;
		for(ScoreDoc score : hits){
			Document doc = searcher.doc(score.doc);
			int idDoc = Integer.parseInt(doc.get("I"));
			if (!(query.getRelevants()==null)&&query.getRelevants().contains(idDoc)){
				relevants++;
				suma += relevants/i;	
			}
			i++;
		}
		return (suma/query.getRelevants().size());
	}
	
}
