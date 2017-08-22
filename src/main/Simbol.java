package main;

public class Simbol {
	int broj;
	boolean proveren = false;
	

	public Simbol(int broj){
		this.broj = broj;
	}

	public boolean isProveren() {
		return proveren;
	}
	
	public void setProveren(boolean proveren) {
		this.proveren = proveren;
	}

	public int getBroj() {
		return broj;
	}

	public void setBroj(int broj) {
		this.broj = broj;
	}
}
