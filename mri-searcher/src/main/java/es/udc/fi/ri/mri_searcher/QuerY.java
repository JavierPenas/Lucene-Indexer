package es.udc.fi.ri.mri_searcher;

import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class QuerY {
	private  int id;
	private  String text;
	private  Query query;
	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	private  List<Integer> relevants = null;
	private  BooleanQuery.Builder builder = new BooleanQuery.Builder();
	private float p10;
	private float p20;
	private float r10;
	private float r20;
	
	
	public float getP10() {
		return p10;
	}

	public void setP10(float p10) {
		this.p10 = p10;
	}

	public float getP20() {
		return p20;
	}

	public void setP20(float p20) {
		this.p20 = p20;
	}

	public float getR10() {
		return r10;
	}

	public void setR10(float r10) {
		this.r10 = r10;
	}

	public float getR20() {
		return r20;
	}

	public void setR20(float r20) {
		this.r20 = r20;
	}

	public QuerY(int id, String text,List<Integer> relevants) {
		this.id = id;
		this.text = text;
		this.relevants = relevants;
	}
	
	public void addTerm(Query query, BooleanClause.Occur clause){
		builder.add(query, clause);
	}
	
	public Query getQueryExpandida (){
		return builder.build();
	}
	public List<Integer> getRelevants() {
		return relevants;
	}

	public void setRelevants(List<Integer> relevants) {
		this.relevants = relevants;
	}

	public QuerY(){
		
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}



	
}
