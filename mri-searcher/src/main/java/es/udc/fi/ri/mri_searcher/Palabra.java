package es.udc.fi.ri.mri_searcher;

import java.util.ArrayList;
import java.util.List;

public class Palabra {
	private String palabra;
	private double totalTermFreq; //Frecuencia palabra en colección
	private double docFreq; //frecuencia palabra en documento
	private double pwd;
	private Double pwr;
	private List<PalabraDoc> listaPalabras = new ArrayList<PalabraDoc>();
	
	public Palabra(String palabra, double totalTermFreq, double docFreq) {
		super();
		this.palabra = palabra;
		this.totalTermFreq = totalTermFreq;
		this.docFreq = docFreq;
	}
	
	public void addPalabra(PalabraDoc p){
		listaPalabras.add(p);
	}
	public List<PalabraDoc> getPalabras(){
		return this.listaPalabras;
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
