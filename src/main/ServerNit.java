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
				System.out.println("sad treba da primi, ceka ga");
				paket = (Paket) primiPaket.readObject();
				System.out.println("Primio je objekat");
				if (paket.getType() == Paket.USERNAME) {
					String trenutnoIme = paket.getPoruka();
					System.out.println("Ime koje proveravamo je: " + trenutnoIme);
					boolean imaGa = false;
					for (int i = 0; i < klijenti.size(); i++) {
						if (klijenti.get(i) != this && klijenti.get(i).ime.equals(trenutnoIme)) {
							imaGa = true;
							saljiPaket.writeObject(new Paket(Paket.INVALID_USERNAME));
						}
					}
					if (!imaGa) {
						System.out.println("Izgleda da ga nema");
						this.ime = trenutnoIme;
						saljiPaket.writeObject(new Paket(Paket.VALID_USERNAME));
					}

				}
				
				if (paket.getType() == Paket.LIST_REQUEST) {
					LinkedList<String> lista = new LinkedList<>();
					for (int i = 0; i < klijenti.size(); i++) {
						String tempIme = klijenti.get(i).ime;
						if (tempIme != null && tempIme != this.ime) {
							System.out.println("Dodajem klijenta + " + tempIme);
							lista.add(tempIme);
						}
					}
					if (lista.isEmpty()){
					saljiPaket.writeObject(new Paket(lista, "NEMA IGRACA"));
						System.out.println("Poslao paket da nema igraca");
					}
					else{
						saljiPaket.writeObject(new Paket(lista, null));
						System.out.println("Poslao listu gde ima igraca!");
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
		} catch (Exception e) {
			e.printStackTrace();
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
