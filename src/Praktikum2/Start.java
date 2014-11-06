package Praktikum2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;



import Praktikum2.POP3Client;
import Praktikum2.POP3Server;

public class Start {

	private static ServerSocket server;
	private static int port = 0;
	private static BufferedReader console = new BufferedReader(
			new InputStreamReader(System.in));


	
	
	public static void main(String[] args) {

		POP3Client p = new POP3Client();
		//p.addKonto(new EmailKonto("test3@smart-mail.de", "kurix",
		//		"pop.smart-mail.de", 110));
		p.addKonto(new EmailKonto("bai4rnpC", "B0ivhFKc","lab30.cpt.haw-hamburg.de", 11000));

		

		while (server == null) {
			System.out
					.println("Geben Sie den gewünschten Port an (Eingaben '0'=>nächster freier Port zuweisen oder zwichen '1' und 2^16):\n");
			String eingabe = "";
			try {
				eingabe = console.readLine();
			} catch (IOException e) {
				System.out.println("I/O Fehler");
			}
			try {
				port = Integer.parseInt(eingabe);
			} catch (NumberFormatException ex) {
				System.out
						.println("Der Wert '"
								+ eingabe
								+ "' kann nicht als Port verwendet werden. Bitte geben Sie eine Zahl ein!");
				continue;
			}

			try {
				server = new ServerSocket(port); //0, InetAddress.getByName("localhost")
			} catch (IOException ex) {
				System.out.println("Der Wert '" + eingabe
						+ "' kann nicht als Port verwendet werden.");
				continue;
			} catch (IllegalArgumentException ex) {
				System.out
						.println("Der Wert '"
								+ eingabe
								+ "' kann nicht als Port verwendet werden. Es sind nur positive Ganzezahlen erlaubt zwischen 0 und 2^16!");
				continue;
			}
		}

		POP3Server pop3server = new POP3Server(server);
		
		p.start();
		pop3server.start();

	}

}
