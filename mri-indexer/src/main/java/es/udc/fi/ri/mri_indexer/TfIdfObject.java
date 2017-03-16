package es.udc.fi.ri.mri_indexer;

import org.apache.lucene.index.Term;

public class TfIdfObject implements Comparable<TfIdfObject> {
	private final double tf;
	private final double idf;
	private final int df;
	private final int docId;
	private final String term;
	private final double tfIdf;
	
	public TfIdfObject(final double tf,final int df, final double idf, final int docId, final String term){
		this.tf = tf;
		this.idf = idf;
		this.docId = docId;
		this.term = term;
		this.df = df;
		this.tfIdf = tf*idf;
	}

	public Double getTfIdf(){
		return this.tfIdf;
	}
	public int getdf(){
		return this.df;
	}
	
	public int compareTo(TfIdfObject o1) {
		if((this.tfIdf)>(o1.getTfIdf())){
			return 1;
		}else{
			return 0;
		}
	}

	@Override
	public String toString() {
		return "TfIdfObject [tf=" + tf + " df= "+df+", idf=" + idf + ", docId=" + docId + ", term=" + term + ", tfIdf=" + tfIdf
				+ "]";
	}
	
	
}
