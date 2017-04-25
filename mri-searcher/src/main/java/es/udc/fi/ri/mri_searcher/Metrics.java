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
		if(relevants!=0){
			System.out.println("P@10: "+relevants);
		}
		
	}
}
