package RNP1;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class MultiServer implements Runnable {
	private static Thread listener;
	private static ServerSocket server;
	private static int port = 0;
	private static BufferedReader console = new BufferedReader(
			new InputStreamReader(System.in));
	private static boolean shutdown = false;
	private static int akzeptierteClient = 0;


	public static void main(String args[]) {
		new MultiServer();
	}

	

	public void run() {
		try {
			Socket client = null;
	
			while (server != null && !shutdown) {

				client = server.accept(); // Listener Thread wartet immer hier
											// bis ein nächster Client kommt
				if (client != null && !shutdown) {
				
					LogicServerThread neuerLogicThread = new LogicServerThread(client);
					neuerLogicThread.start();
					akzeptierteClient ++;
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
		System.out.println("Aktuell verbundene Clients: " + akzeptierteClient +" | Status ShutdownFlag: " +shutdown);
	}

	public static int getAkzeptierteClients() {
		return akzeptierteClient;
	}

	public static boolean getShutDownFlag() {
		return shutdown;
	}

	protected static void setShutdownFlag() {
		MultiServer.shutdown = true;
	}

	protected static void decrementAkzeptierteClient() {
		MultiServer.akzeptierteClient -= 1;
	}

	///Server Konfiguration /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public MultiServer() {
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
