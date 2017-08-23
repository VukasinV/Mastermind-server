package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ServerNit extends Thread {
	Socket soketZaKomunikaciju = null;
	LinkedList<ServerNit> klijenti;
	public ObjectOutputStream saljiPaket = null;
	Game game;
	public ObjectInputStream primiPaket = null;
	String ime;
	boolean mojaIgra;
	String imeProtivnika;
	boolean uIgric = false;
	Map<ServerNit, ServerNit> trenutneIgre = new HashMap<>();

	public ServerNit(Socket soket, LinkedList<ServerNit> klijent) {
		this.soketZaKomunikaciju = soket;
		this.klijenti = klijent;
	}

	public void run() {
		Paket paket;
		try {

			saljiPaket = new ObjectOutputStream(soketZaKomunikaciju.getOutputStream());
			primiPaket = new ObjectInputStream(soketZaKomunikaciju.getInputStream());

			while (true) {
				System.out.println("Cekanje paketa od klijenta...");
				paket = (Paket) primiPaket.readObject();
				System.out.println("PRIJEM PAKETA");
				if (paket.getType() == Paket.USERNAME) {
					String trenutnoIme = paket.getPoruka();
					System.out.println("Ime koje proveravamo je: " + trenutnoIme);
					boolean imaGa = false;
					for (int i = 0; i < klijenti.size(); i++) {
						if (klijenti.get(i).ime != null && klijenti.get(i) != this
								&& klijenti.get(i).ime.equals(trenutnoIme)) {
							imaGa = true;
							saljiPaket.writeObject(new Paket(Paket.INVALID_USERNAME));
						}
					}
					if (!imaGa) {
						System.out.println("Ime je uredu, uspesno logovanje.");
						this.ime = trenutnoIme;
						saljiPaket.writeObject(new Paket(Paket.VALID_USERNAME));
					}

				}

				if (paket.getType() == Paket.LIST_REQUEST) {
					LinkedList<String> lista = new LinkedList<>();
					System.out.print("Igraci koji su online: ");
					for (int i = 0; i < klijenti.size(); i++) {
						String tempIme = klijenti.get(i).ime;
						if (tempIme != null && tempIme != this.ime && !klijenti.get(i).uIgric) {
							System.out.print(tempIme + "  ");
							lista.add(tempIme);
						}
					}
					if (lista.isEmpty()) {
						saljiPaket.writeObject(new Paket(Paket.NO_PLAYERS_ONLINE));
						System.out.println("TRENUTNO NEMA IGRACA NA SERVERU! (poslato: " + ime + ")");
					} else {
						// for (int i = 0; i < klijenti.size(); i++) {
						// if (klijenti.get(i).ime == this.ime) {
						saljiPaket.writeObject(new Paket(lista, this.ime));
						// System.out.println("POSLAT SPISAK IGRACA KLIJENTU: "
						// + klijenti.get(i).ime);
						// }
						// }
						// saljiPaket.writeObject(new Paket(lista, null));
						// System.out.println("POSLAT SPISAK IGRACA KLIJENTU: "
						// + ime);
					}
				}
				if (paket.getType() == Paket.MESSAGE && paket.getPoruka() != null
						&& paket.getPoruka().equals("ZATVORI NIT")) {
					break;
				}

				if (paket.getType() == Paket.CHOOSEN_PLAYER) {
					System.out.println("Usao u potragu za izabranim ");
					for (int i = 0; i < klijenti.size(); i++) {
						System.out.println("usao u for..." + paket.getPoruka());
						if (klijenti.get(i).ime.equals(paket.getPoruka())) {
							System.out.println("Usao u if...");
							klijenti.get(i).saljiPaket.writeObject(new Paket(Paket.CHOOSEN_PLAYER, this.ime));
						}
					}
				}

				if (paket.getType() == Paket.ACCEPTED) {
					System.out.println("Accepted server..");

					for (int i = 0; i < klijenti.size(); i++) {
						System.out.println("usao u for...");
						if (klijenti.get(i).ime.equals(paket.getPoruka())) {
							System.out.println("Usao u if...");
							System.out.println(
									"Ime izabranog je " + this.ime + " A onog ko izaziva " + paket.getPoruka());
							klijenti.get(i).saljiPaket.writeObject(new Paket(Paket.ACCEPTED, "izazvac si"));
							saljiPaket.writeObject(new Paket(Paket.ACCEPTED, "izazvan si"));
							klijenti.get(i).mojaIgra = true;
							klijenti.get(i).game = new Game();
							uIgric = true;
							klijenti.get(i).uIgric = true;
							imeProtivnika = klijenti.get(i).ime;
							klijenti.get(i).imeProtivnika = this.ime;
						}
					}

				}

				if (paket.getType() == Paket.COMBINATION) {
					int a = Integer.parseInt(paket.getPoruka().split(",")[0]);
					int b = Integer.parseInt(paket.getPoruka().split(",")[1]);
					int c = Integer.parseInt(paket.getPoruka().split(",")[2]);
					int d = Integer.parseInt(paket.getPoruka().split(",")[3]);
					int red = paket.getRed();
					if (mojaIgra) {
						int brojPogodjenihNaMestu = game.vratiBrojPogodjenih(a, b, c, d, true);
						int brojPogodjenihNisuNaMestu = game.vratiBrojPogodjenih(a, b, c, d, false);
						
						for (int i = 0; i < klijenti.size(); i++) {
							if (klijenti.get(i).ime.equals(imeProtivnika)) {
								klijenti.get(i).saljiPaket.writeObject(paket);
								klijenti.get(i).saljiPaket.writeObject(new Paket(Paket.REZ, brojPogodjenihNaMestu + "," + brojPogodjenihNisuNaMestu, red));
								
								if(brojPogodjenihNaMestu == 4){
									klijenti.get(i).saljiPaket.writeObject(new Paket(Paket.WIN, a +","+b+","+c+","+d));
									saljiPaket.writeObject(new Paket(Paket.WIN, a +","+b+","+c+","+d, red));
								}
								
							}
						}
						saljiPaket.writeObject(new Paket(Paket.REZ, brojPogodjenihNaMestu + "," + brojPogodjenihNisuNaMestu, red));
					}
				}
				
				if(paket.getType() == Paket.TURN){
					for (int i = 0; i < klijenti.size(); i++) {
						if (klijenti.get(i).ime.equals(imeProtivnika)) {
							klijenti.get(i).mojaIgra = true;
							this.mojaIgra = false;
						}
					}
				}

				if (paket.getType() == Paket.DECLINED) {

					for (int i = 0; i < klijenti.size(); i++) {
						System.out.println("usao u for...");
						if (klijenti.get(i).ime.equals(paket.getPoruka())) {
							System.out.println("Usao u if...");
							klijenti.get(i).saljiPaket.writeObject(new Paket(Paket.DECLINED, this.ime));
						}
					}
				}
			}

			soketZaKomunikaciju.close();
			System.out.println("Klijent se diskonektovao");
			klijenti.remove(this);
			System.out.println("Uklonio je nit iz nekog razloga");
		} catch (IOException ex) {
			//ex.printStackTrace();
			klijenti.remove(this);
			System.out.println("Desila se greska i klijentska nit se ugasila");
		} catch (Exception e) {
			//e.printStackTrace();
			klijenti.remove(this);
			System.out.println("Desila se greska i klijentska nit se ugasila");		
		}

	}

	public void posaljiObojici(ServerNit a, ServerNit b, String kombinacija) {
		try {
			for (int i = 0; i < klijenti.size(); i++) {
				if (klijenti.get(i).isAlive() && (klijenti.get(i) == a || klijenti.get(i) == b)) {
					klijenti.get(i).saljiPaket.writeObject(new Paket(Paket.COMBINATION, kombinacija));
				}
			}
		} catch (Exception e) {
			System.out.println("Igra je prekinuta!");
		}
	}

	public static boolean hostAvailabilityCheck() {
		try (Socket s = new Socket("localhost", 4444)) {
			return true;
		} catch (IOException ex) {
			/* ignore */
		}
		return false;
	}
}
