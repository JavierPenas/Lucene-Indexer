package es.udc.fi.ri.mri_searcher;

import java.util.List;

public class QuerY {
	private  int id;
	private  String text;
	private  List<Integer> relevants = null;
	
	public QuerY(int id, String text,List<Integer> relevants) {
		this.id = id;
		this.text = text;
		this.relevants = relevants;
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
