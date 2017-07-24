package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class ServerNit extends Thread {
	Socket soketZaKomunikaciju = null;
	LinkedList<ServerNit> klijenti;
	public static ObjectOutputStream saljiPaket = null;
	public static ObjectInputStream primiPaket = null;
	String ime;
	String imeProtivnika;

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
						if (klijenti.get(i).ime != null && klijenti.get(i) != this && klijenti.get(i).ime.equals(trenutnoIme)) {
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
						if (tempIme != null && tempIme != this.ime) {
							System.out.print(tempIme + "  ");
							lista.add(tempIme);
						}
					}
					if (lista.isEmpty()){
					saljiPaket.writeObject(new Paket(Paket.NO_PLAYERS_ONLINE));
						System.out.println("TRENUTNO NEMA IGRACA NA SERVERU! (poslato: " + ime + ")");
					}
					else{
						for (int i = 0; i < klijenti.size(); i++) {
							if (klijenti.get(i).ime == this.ime) {
								saljiPaket.writeObject(new Paket(lista, null));
								System.out.println("POSLAT SPISAK IGRACA KLIJENTU: " + klijenti.get(i).ime);
							}
						}
//						saljiPaket.writeObject(new Paket(lista, null));
//						System.out.println("POSLAT SPISAK IGRACA KLIJENTU: " + ime);
					}
				}
				if (paket.getType() == Paket.MESSAGE && paket.getPoruka() != null && paket.getPoruka().equals("ZATVORI NIT")) {
					break;
				}
			}

			soketZaKomunikaciju.close();
			System.out.println("Klijent se diskonektovao");
			klijenti.remove(this);
			System.out.println("Uklonio je nit iz nekog razloga");
		} catch (IOException ex) {
			ex.printStackTrace();
			klijenti.remove(this);
			System.out.println("Desila se greska i klijentska nit se ugasila");
		} catch (Exception e) {
			e.printStackTrace();
			klijenti.remove(this);
			System.out.println("Desila se greska i klijentska nit se ugasila");
			// TODO: handle exception
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
