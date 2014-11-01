package Praktikum1;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.io.*;

class Client {

	private static Socket server = null;
	private static BufferedReader console = new BufferedReader(
			new InputStreamReader(System.in));
	private static int port = 55555;
	private static String eingabe = "";
	private static String adresse = "";
	private static boolean beenden = false;

	public static void main(String[] args) {

		System.out
				.println("Herzlich Willkommen beim Steiwoltischen Netzwerk Spaﬂ!  - CLIENT!!!!");
		while (beenden != true) {
			try {
				verbindungsAufbau();
			} catch (Exception e) {
				System.out
						.println("Es konnte keine Verbindung zum Server hergestellt werden.");
			}
		}

	}

	private static void verbindungsAufbau() {
		while (server == null) {
			try {
				System.out.println("Bitte geben Sie den Port ein:");
				eingabe = console.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("I/O Fehler");
			}

			try {
				port = Integer.parseInt(eingabe); // Falls kein Int Wert
													// erstellt werden kann
			} catch (NumberFormatException ex) {
				System.out
						.println("Der Wert '"
								+ eingabe
								+ "' kann nicht als Port verwendet werden. Bitte geben Sie eine Zahl ein!");
				continue;
			}
			// Adresse erfragen
			try {
				System.out
						.println("Bitte geben Sie die IP oder den Computernamen ein:");
				adresse = console.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("I/O Fehler");
			}

			try {

				Scanner in = null;
				PrintWriter out = null;
				String rueckgabe = "";
				server = new Socket(adresse, port);
				in = new Scanner(server.getInputStream());
				out = new PrintWriter(server.getOutputStream(), true);
				
			
				
				while (true) {

					eingabe = console.readLine();
					out.println(eingabe);
					rueckgabe = in.nextLine();
					
					byte[] bytes = rueckgabe.getBytes();
					Charset.forName("UTF-8").decode(ByteBuffer.wrap(bytes));
					System.out.println(bytes + rueckgabe +"\n");
					
					if (rueckgabe.equals("BYE")) {
						System.out
								.println("Die Verbindung zum Server wurde getrennt!");

						out.close();
						in.close();
						break;

					}
				}

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {

				if (server != null)
					try {

						server.close();
					} catch (IOException e) {
					}
			}

		}
	}
}
