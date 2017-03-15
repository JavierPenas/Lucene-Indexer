package es.udc.fi.ri.mri_indexer;

import org.apache.lucene.index.Term;

public class TfIdfObject implements Comparable<TfIdfObject> {
	private final int tf;
	private final double idf;
	private final int docId;
	private final String term;
	private final double tfIdf;
	
	public TfIdfObject(final int tf, final double idf, final int docId, final String term){
		this.tf = tf;
		this.idf = idf;
		this.docId = docId;
		this.term = term;
		this.tfIdf = tf*idf;
	}

	public double getTfIdf(){
		return this.tfIdf;
	}
	
	public int compareTo(TfIdfObject o1) {
		if((this.tfIdf)<(o1.getTfIdf())){
			return 1;
		}else{
			return 0;
		}
	}

	@Override
	public String toString() {
		return "TfIdfObject [tf=" + tf + ", idf=" + idf + ", docId=" + docId + ", term=" + term + ", tfIdf=" + tfIdf
				+ "]";
	}
	
	
}
