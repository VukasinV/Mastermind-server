package main;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
	int brPogodjenihNaMestu = 0;
	int brPogodjenih = 0;
	private LinkedList<String> users = new LinkedList<>();
	 int q = ThreadLocalRandom.current().nextInt(1, 7);
	 int w = ThreadLocalRandom.current().nextInt(1, 7);
	 int e = ThreadLocalRandom.current().nextInt(1, 7);
	 int r = ThreadLocalRandom.current().nextInt(1, 7);
	// pik......1
	// tref.....2
	// herc.....3
	// karo.....4
	// zvezda...5
	// joker....6

	public void proveriResenje(int q, int w, int e, int r, int q1, int w1, int e1, int r1) {
		LinkedList<Simbol> resenje = new LinkedList<>();
		LinkedList<Simbol> pokusaj = new LinkedList<>();

		// brojeve sam stavljao u bojekat "Simbol" koji pored broja ima i 
		// boolean "proveren" koji ce mi pomoci da proverim koje brojeve 
		// sam vec pregledao i brojao, a koje ne
		
		resenje.addLast(new Simbol(q));
		resenje.addLast(new Simbol(w));
		resenje.addLast(new Simbol(e));
		resenje.addLast(new Simbol(r));

		pokusaj.addLast(new Simbol(q1));
		pokusaj.addLast(new Simbol(w1));
		pokusaj.addLast(new Simbol(e1));
		pokusaj.addLast(new Simbol(r1));

		// Prolazi kroz obe liste i broji one koji su na mestu
		// a menjajuci "proveren" na true
		for (int i = 0; i < 4; i++) {
			if (resenje.get(i).getBroj() == pokusaj.get(i).getBroj()) {
				brPogodjenihNaMestu++;
				resenje.get(i).setProveren(true);
				pokusaj.get(i).setProveren(true);
			}
		}
		
		// Prolazi kroz obe liste i broji one koji su pogodjeni,
		// ali nisu na mestu
		for (int i = 0; i < 4; i++) {
			if (!pokusaj.get(i).isProveren()) {
				for (int j = 0; j < 4; j++) {
					if (!resenje.get(j).isProveren() && pokusaj.get(i).getBroj() == resenje.get(j).getBroj()) {
						brPogodjenih++;
						resenje.get(j).setProveren(true);
						pokusaj.get(i).setProveren(true);
						break;
					}
				}
			}
		}
	}

	// "naMestuIliNe" boolean sluzi da bi odredio da li vracam brPogodjenihNaMestu 
	// ili samo brPogodjenih, da ne bi pisao dve odvojene metode
	public int vratiBrojPogodjenih(int a, int b, int c, int d, boolean naMestuIliNe) {
		brPogodjenih = 0;
		brPogodjenihNaMestu = 0;
		proveriResenje(this.q, this.w, this.e, this.r, a, b, c, d);
		if (naMestuIliNe) {
			return brPogodjenihNaMestu;
		} else {
			return brPogodjenih;
		}
	}

	public int getQ() {
		return q;
	}

	public void setQ(int q) {
		this.q = q;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getE() {
		return e;
	}

	public void setE(int e) {
		this.e = e;
	}

	public LinkedList<String> getUsers() {
		return users;
	}

	public void setUsers(LinkedList<String> users) {
		this.users = users;
	}

}
