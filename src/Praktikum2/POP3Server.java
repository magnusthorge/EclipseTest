package Praktikum2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import Praktikum2.Logger;

public class POP3Server extends Thread {

	private static final String BENUTZER = "Rechnernetze";
	private static final String PASSWORT = "B0ivhFKc";

	private ServerSocket serverSocket;
	private Socket clientVerbindung;

	private BufferedReader reader;
	private BufferedWriter writer;

	private ArrayList<EMail> mails;
	InputStream in;
	OutputStream out;

	Logger logger;

	public POP3Server(ServerSocket server) {
		serverSocket = server;
		logger = new Logger("Server");
		logger.setDebug(true);
	}

	@Override
	public void run() {
		while (true) {
			try {
				clientVerbindung = serverSocket.accept();
				writer = new BufferedWriter(new OutputStreamWriter(
						clientVerbindung.getOutputStream()));
				reader = new BufferedReader(new InputStreamReader(
						clientVerbindung.getInputStream()));
				logger.open();
				this.send("+OK Server bereit");
				mails = getAlleMails();
				while (!clientVerbindung.isClosed() && !this.authentifiziere())
					;
				while (!clientVerbindung.isClosed()) {
					transaktion();
				}
				logger.log("Session beendet");
				logger.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void send(String nachricht) {
		try {
			writer.write(nachricht + "\r\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.log("Server: " + nachricht);
	}

	private boolean authentifiziere() throws IOException {
		logger.log("Warte auf Benutzername");
		String eingehendeNachricht = reader.readLine();
		logger.log("Client: " + eingehendeNachricht);
		if (eingehendeNachricht == null) {
			verarbeiteQuit();
			return false;
		}
		while (!eingehendeNachricht.startsWith("USER")) {
			if (eingehendeNachricht.startsWith("QUIT")) {
				verarbeiteQuit();
				return false;
			}
			send("-ERR Befehl unbekannt");
			eingehendeNachricht = reader.readLine();
			logger.log("Client: " + eingehendeNachricht);
		}

		// Anmeldevorgang
		String benutzername = eingehendeNachricht.substring(5);

		if (!benutzername.equals(BENUTZER)) {
			send("-ERR Benutzername nicht gefunden");
			return false;
		}

		send("+OK Passwort?");
		eingehendeNachricht = reader.readLine();
		String sentPassword = eingehendeNachricht.substring(5);
		logger.log(eingehendeNachricht);
		while (!eingehendeNachricht.startsWith("PASS") || !sentPassword.equals(PASSWORT)) {
			if (!eingehendeNachricht.startsWith("PASS")) {
				send("-ERR Passwort erwartet");
				eingehendeNachricht = reader.readLine();
			}
			
			sentPassword = eingehendeNachricht.substring(5);
			if (!sentPassword.equals(PASSWORT)) {
				send("-ERR Passwort ist ungültig");
				eingehendeNachricht = reader.readLine();
			}
		}

		send("+OK");
		return true;
	}

	private void transaktion() throws IOException {
		String eingehendeNachricht = reader.readLine();
		String befehl = "";
		try {
			befehl = eingehendeNachricht.substring(0, 4);
		} catch (StringIndexOutOfBoundsException ex) {
			befehl = "error";
		}
		logger.log("Client: " + eingehendeNachricht);
		switch (befehl) {
		case "STAT":
			verarbeiteStat();
			break;
		case "LIST":
			verarbeiteList(eingehendeNachricht);
			break;
		case "UIDL":
			verarbeiteUidl(eingehendeNachricht);
			break;
		case "RETR":
			verarbeiteRetr(eingehendeNachricht.substring(5));
			break;
		case "DELE":
			verarbeiteDele(eingehendeNachricht.substring(5));
			break;
		case "NOOP":
			verarbeiteNoop();
			break;
		case "RSET":
			verarbeiteRset();
			break;
		case "QUIT":
			verarbeiteQuit();
			break;
		default:
			verarbeiteDefault();
		}
	}

	private void verarbeiteDefault() { // Bei Falscher Eingabe
		send("-ERR Befehl wurde nicht gefunden");
	}

	private void verarbeiteRset() { // RSET setzt alle DELE-Kommandos zur�ck.
		for (EMail mail : mails) {
			if (mail.markiertZumLoschen()) {
				mail.setzeAUfMarkiertZumLoeschen(false);
			}
		}
		send("+OK alle Nachrichten wurden demarkiert");
	}

	private void verarbeiteNoop() { // keine Funktion, der Server antwortet mit
									// +OK
		send("+OK");
	}

	private void verarbeiteDele(String n) { // l�scht die n-te E-Mail am Server.
		int arg = Integer.parseInt(n);
		if (arg - 1 > mails.size()) {
			send("-ERR Nachricht wurde nicht gefunden");
		} else {
			mails.get(arg - 1).setzeAUfMarkiertZumLoeschen(true);
			send("+OK Nachricht " + arg + " ist markiert zum l�schen");
		}
	}

	private void verarbeiteRetr(String n) { // holt die n-te E-Mail vom
											// E-Mail-Server.
		int arg = Integer.parseInt(n);
		if (arg - 1 > mails.size()) {
			send("-ERR Nachricht wurde nicht gefunden");
		} else {
			EMail mail = mails.get(arg - 1);
			if (mail.markiertZumLoschen()) {
				send("-ERR Nachricht ist markiert zum löschen");
			} else {
				send("+OK Nachricht folgt:");
				send(mail.getInhalt());
				send(".");
			}
		}
	}

	private void verarbeiteUidl(String nachricht) { // zeigt die eindeutige ID
													// der E-Mail an
		int arg = 0;
		if (nachricht.length() > 4)
			arg = Integer.parseInt(nachricht.substring(nachricht
					.lastIndexOf(" ")));
		if (arg != 0 && arg <= mails.size()) {
			if (mails.get(arg - 1).markiertZumLoschen()) {
				send("-ERR Nachricht ist zum l�schen markiert");
			} else {
				send("+OK " + arg + " " + mails.get(arg - 1).getUID());
			}
		} else if (arg != 0) { // Argument bigger then number of mails
			send("-ERR Nachricht nicht gefunden");
		} else { // No Argument given. All Mails listed
			send("+OK Uidl Auflistung folgt:");
			int i = 1;
			for (EMail mail : mails) {
				send(i + " " + mail.getUID());
				i++;
			}
			send(".");
		}
	}

	private void verarbeiteList(String nachricht) { // liefert die Anzahl und
													// die Gr��e der (n-ten)
													// E-Mail(s).
		send("+OK Auflistung folgt:");
		int arg = 0;
		if (nachricht.length() > 4)
			arg = Integer.parseInt(nachricht.substring(nachricht
					.lastIndexOf(" ")));
		if (arg != 0 && arg <= mails.size()) {
			send("+OK " + arg + " " + mails.get(arg - 1).getGroesseInByte());
		} else if (arg != 0) { // Argument bigger then number of mails
			String response = "-ERR Nachricht nicht gefunden";
			logger.log(response);
			send(response);
		} else { // No Argument given. All Mails listed
			int i = 1;
			for (EMail mail : mails) {
				send(i + " " + mail.getGroesseInByte());
				i++;
			}
			send(".");
		}
	}

	private void verarbeiteStat() {
		int anzahlDerNachrichten = mails.size();
		long groesseDerNachrichten = 0;
		for (EMail m : mails) {
			groesseDerNachrichten += m.getGroesseInByte();
		}
		send("+OK " + anzahlDerNachrichten + " " + groesseDerNachrichten);
	}

	private void verarbeiteQuit() {
		try {
			send("+OK");
			clientVerbindung.close();
			deleteMarkedMails();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void deleteMarkedMails() {
		for (EMail mail : mails) {
			if (mail.markiertZumLoschen()) {
				File mailFile = new File(EMail.NACHRICHTENVERZEICHNIS
						+ mail.getUID() + ".email");
				mailFile.delete();
				logger.log("Nachricht mit UID " + mail.getUID()
						+ " wurde gel�scht");
			}

		}
	}

	private ArrayList<EMail> getAlleMails() {
		ArrayList<EMail> rueckgabeListe = new ArrayList<EMail>();
		File directory = new File(EMail.NACHRICHTENVERZEICHNIS);
		for (String filename : directory.list()) {
			rueckgabeListe.add(EMail.ladeEmailAusDatei(filename));
		}
		return rueckgabeListe;
	}

}
