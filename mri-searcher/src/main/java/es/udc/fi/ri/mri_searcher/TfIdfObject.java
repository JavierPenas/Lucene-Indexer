package es.udc.fi.ri.mri_searcher;

public class TfIdfObject {
	private Double tf;
	private Double idf;
	private Double tfIdf;
	
	
	
	public TfIdfObject(Double tf, Double idf) {
		super();
		this.tf = tf;
		this.idf = idf;
		this.tfIdf = tf*idf;
	}
	
	public Double getTf() {
		return tf;
	}
	public void setTf(Double tf) {
		this.tf = tf;
	}
	public Double getIdf() {
		return idf;
	}
	public void setIdf(Double idf) {
		this.idf = idf;
	}

	public Double getTfIdf() {
		return tfIdf;
	}
	
	
}
