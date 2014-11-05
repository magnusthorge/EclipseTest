package RNP2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LogicServerThread extends Thread {
	private Socket s;
	private Scanner in;
	private PrintWriter out;
	private String rueckgabe;
	private String clientEingabe = "";
	private String[] clientSplitter;

	public String ip;
	public String nr;

	/*
	 * Im Konstruktor vom LogicClient werden zunächst die Streams über das
	 * Socket abgefragt und in Datenelementen gespeichert.
	 */

	public LogicServerThread(Socket s) throws IOException {
		this.s = s;
		out = new PrintWriter(s.getOutputStream(), true); // s.getOutputStream();
		in = new Scanner(s.getInputStream()); // s.getInputStream();
		this.ip = s.getInetAddress().toString();
	}

	/*
	 * In run() werden anschließend Zeichen eingelesen und an den Client
	 * zurückgeschrieben, bis keine Daten mehr vorhanden sind.
	 */
	public void run() {
		while (true) {
			clientEingabe = "";
			rueckgabe = "";

			try {
				clientEingabe = in.nextLine(); // Liest die Nachricht ein
			} catch (Exception e) {
				System.out.println("Verbindung zum CLient wurde UNERWARTET unterbrochen!");
				in.close();
				out.close();
				MultiServer.decrementAkzeptierteClient();
				MultiServer.ausgabeMessage();
				break;
			}
			clientSplitter = clientEingabe.split(" "); // Splittet per
														// Leerzeichen in
														// Befehl = [0] und
														// Wort = [1]
			if (clientSplitter[0].equals("BYE")) {
				rueckgabe = "BYE";
			} else {

				if (clientSplitter.length < 2) {
					rueckgabe = "Der Befehl muss 'Befehlswort 'Zeichenkette' aufgebaut sein! ";
				} else {
					if (clientSplitter.length > 2) {
						String[] temp = clientSplitter;
						clientSplitter = new String[2];
						clientSplitter[0] = temp[0];
						clientSplitter[1] = "";
						for (int i = 1; i < temp.length; i++) {
							clientSplitter[1] += temp[i] + " ";
						}

					}
					handleOperation(clientSplitter);
				}
			}
			try {
				out.println(rueckgabe);
				if (rueckgabe.equals("BYE")) {
					MultiServer.decrementAkzeptierteClient();
					MultiServer.ausgabeMessage();
					out.close();
					in.close();
					
					// Wenn ShutDownFlag = true und Clients = 0 -> Server
					// beenden
					if (MultiServer.getAkzeptierteClients() == 0
							&& MultiServer.getShutDownFlag()) {
						MultiServer.stop();
					}
					// Beendet sich selbst
					break;
				}
			} catch (Exception e) {
				// /Wenn der Client die Verbindung abgebrochen hat
				MultiServer.decrementAkzeptierteClient();
				MultiServer.ausgabeMessage();
				out.close();
				in.close();

				break;
			}
		}
	}

	// Verarbeitet die einzelnen Befehlsoperationen
	private void handleOperation(String[] operation) {
		String befehl = operation[0];
		rueckgabe = "";

		if (befehl.equals("LOWERCASE")) {
			rueckgabe = operation[1].toLowerCase();
		} else if (befehl.equals("UPPERCASE")) {
			rueckgabe = operation[1].toUpperCase();
		} else if (befehl.equals("REVERT")) {
			rueckgabe = (new StringBuilder(operation[1]).reverse()).toString();
		} else if (befehl.equals("SHUTDOWN")) {
			if (operation[1].equals("Kurix")) {
				MultiServer.setShutdownFlag();
				rueckgabe = "OK_BYE";
			} else {
				rueckgabe = "Falsches Passwort!";
			}

		} else {
			rueckgabe = "Ungültiger Befehl";
		}
	}
}
