package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import main.ServerNit;

public class Server {
 static LinkedList<ServerNit> klijenti = new LinkedList<ServerNit>();

	public static void main(String[] args) {
		int port = 4444;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		Socket klijentSoket = null;
		try {
			ServerSocket serverSoket = new ServerSocket(port);

			while (true) {
				klijentSoket = serverSoket.accept();
				 ServerNit novi = new ServerNit(klijentSoket, klijenti);
				 klijenti.addLast(novi);
				 novi.start();
				System.out.println("Novi korisnik je konektovan na server.");
			}
		} catch (IOException e) {
			System.out.println(e);
		}

	}

}

