package es.udc.fi.ri.mri_searcher;

public class Palabra {
	private String palabra;
	private double totalTermFreq; //Frecuencia palabra en colección
	private double docFreq; //frecuencia palabra en documento
	private double pwd;
	private Double pwr;
	
	public Palabra(String palabra, double totalTermFreq, double docFreq) {
		super();
		this.palabra = palabra;
		this.totalTermFreq = totalTermFreq;
		this.docFreq = docFreq;
	}
	
	public Double getPwr() {
		return pwr;
	}

	public void setPwr(double pwr) {
		this.pwr = pwr;
	}

	public double getPwd() {
		return pwd;
	}

	public void setPwd(double pwd) {
		this.pwd = pwd;
	}

	public String getPalabra() {
		return palabra;
	}
	public void setPalabra(String palabra) {
		this.palabra = palabra;
	}
	public double getTotalTermFreq() {
		return totalTermFreq;
	}
	public void setTotalTermFreq(double totalTermFreq) {
		this.totalTermFreq = totalTermFreq;
	}
	public double getDocFreq() {
		return docFreq;
	}
	public void setDocFreq(double docFreq) {
		this.docFreq = docFreq;
	}
	
	
	
}
