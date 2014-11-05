package RNP2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class POP3Server implements Runnable {
	private static Thread listener;
	private static ServerSocket server;
	private static int port = 0;
	private static BufferedReader console = new BufferedReader(
			new InputStreamReader(System.in));
	private static boolean shutdown = false;
	private static int akzeptierteClient = 0;
	private static String eingabe = "";

	public static void main(String args[]) {

		System.out
				.println("###################################################################");
		System.out
				.println("###################################################################");
		System.out
				.println("####################### POP3 Server -Rechnernetze #################");
		System.out
				.println("####################### Süwolto & Staib           #################");
		System.out
				.println("####################### 06.11.2014                #################");
		System.out
				.println("###################################################################");
		System.out
				.println("###################################################################\n\n");

		while(!shutdown){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		System.out
				.println("----------------------------------------------------------------");
		System.out
				.println("| Hauptmenü | Auswahlmenü                                       |");
		System.out
				.println("| Client    | 1) Konfiguieren | 2) Starten| 3) Beenden          |");
		System.out
				.println("| Server    | 1) Konfiguieren | 2) Starten| 3) Beenden          |");
		System.out
				.println("----------------------------------------------------------------");
		System.out
				.println("Beispiel: 'Client 1' für Konfiguration des Clients\n");
		System.out.println("Eingabe:");

		try {

			switch (eingabeVerarbeiten(console.readLine())) {
			case 11:
				System.out.println("Client Konfig");
				break;
			case 12:
				System.out.println("Client Start");
				break;
			case 13:
				System.out.println("Client Stop");
				break;
			case 24:

				break;
			case 25:

				break;
			case 26:

				break;
			default:

				System.out.println("Falsche Eingabe!");

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("I/O Fehler");
		}
		}

		// new POP3Server();
	}

	private static int eingabeVerarbeiten(String eingabe) {
		String[] clientSplitter= eingabe.split(" "); // Splittet per Leerzeichen in Befehl = [0] und  Wort = [1]
		int rueckgabe = 0;

		if(clientSplitter.length != 2){
			return rueckgabe;
		}
		if(clientSplitter[1].equals("1")){
			rueckgabe+=1;
		}
		if(clientSplitter[1].equals("2")){
			rueckgabe+=2;
		}
		if(clientSplitter[1].equals("3")){
			rueckgabe+=3;
		}
		if(clientSplitter[0].equals("Client")){
			return (rueckgabe+10);
		}
		if(clientSplitter[0].equals("Server")){
			return (rueckgabe+20);
		}
		return rueckgabe;
	}

	public void run() {
		try {
			Socket client = null;

			while (server != null && !shutdown) {

				client = server.accept(); // Listener Thread wartet immer hier
											// bis ein nächster Client kommt
				if (client != null && !shutdown) {

					LogicServerThread neuerLogicThread = new LogicServerThread(
							client);
					neuerLogicThread.start();
					akzeptierteClient++;
					client = null;
					ausgabeMessage();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void stop() {
		System.out.println("Der Server wurde gestoppt!");
		try {
			server.close();
			server = null;
		} catch (SocketException ex) {
			System.out.println("");
		} catch (IOException ex) {
			System.out.println("");
		} finally {
			listener.interrupt();
			server = null;
		}
	}

	protected static void ausgabeMessage() {
		System.out.println("Aktuell verbundene Clients: " + akzeptierteClient
				+ " | Status ShutdownFlag: " + shutdown);
	}

	public static int getAkzeptierteClients() {
		return akzeptierteClient;
	}

	public static boolean getShutDownFlag() {
		return shutdown;
	}

	protected static void setShutdownFlag() {
		POP3Server.shutdown = true;
	}

	protected static void decrementAkzeptierteClient() {
		POP3Server.akzeptierteClient -= 1;
	}

	// /Server Konfiguration
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public POP3Server() {
		try {
			while (server == null) {
				server = portEinrichten();
			}
		} catch (Exception e) {
			System.out.println("Fehler beim starten des Server!");
		}
		if (server != null) {
			try {
				System.out
						.println("\nServer wurde gestartet! \n-------------------------------------------------------------\n"
								+ "Sie erreichen ihn unter Host Name/Adresse: \n"
								+ Inet4Address.getLocalHost()
								+ " Port: "
								+ server.getLocalPort()
								+ "\n-------------------------------------------------------------");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			listener = new Thread(this);
			listener.start();
		} else {
			System.out.println("Der Port war schon belegt!");
			port = 0;
		}

	}

	private static ServerSocket portEinrichten() throws IOException {
		while (port == 0) {
			System.out
					.println("Geben Sie den gewünschen Port an (Eingaben '0'=>nächster freier Port zuweisen oder zwichen '1' und 2^16):\n");
			String eingabe = "";
			try {
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

			try {
				return new ServerSocket(port);
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
		return null;

	}

}
