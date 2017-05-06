package es.udc.fi.ri.mri_searcher;

public class PalabraDoc {
	private double pd;
	private double pwd;
	private double pqd;
	
	
	
	public PalabraDoc(double pd, double pwd, double pqd) {
		super();
		this.pd = pd;
		this.pwd = pwd;
		this.pqd = pqd;
	}
	public double getPd() {
		return pd;
	}
	public void setPd(double pd) {
		this.pd = pd;
	}
	public double getPwd() {
		return pwd;
	}
	public void setPwd(double pwd) {
		this.pwd = pwd;
	}
	public double getPqd() {
		return pqd;
	}
	public void setPqd(double pqd) {
		this.pqd = pqd;
	}
	
	
}
