package Praktikum2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;





import Praktikum2.Logger;


public class POP3Client extends Thread {
	private Socket socket;

	private BufferedReader reader;
	private BufferedWriter writer;
	
	private ArrayList<EmailKonto> konten;
	
	private Logger logger;
	private final long ZEITABSTAND=(30000); //30 Sekunden Zeitabstand
	
	
	public POP3Client(){
		konten = new ArrayList<EmailKonto>();
		logger = new Logger("Client");
		logger.setDebug(false);
		
	}
	
	public void addKonto(EmailKonto konto) {
		konten.add(konto);
	}
	
	public boolean verbindenZuInternetPOP3(String host, int port) {
		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(host, port));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			logger.log("Verbindung: " + isConnected());
			String antwortVomServer = getAntwortVomServer();
			return (antwortVomServer.startsWith("+OK")); //Wenn +OK zur�ckkommt -> true
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isConnected() { //Hilfsmethode um isConnected aus Socket Objekt ohne Fehler aufzurufen
		return (socket != null && socket.isConnected());
	}
	
	public void schliesseVerbindung() throws IOException {
		if (!isConnected())
			throw new IllegalStateException("Not connected to a host");
		socket.close();
		socket = null;
		reader = null;
		writer = null;
	}
	
	@Override
	public void run() {
		while (true) {
			abholenDerEmails();
			try {
				Thread.sleep(ZEITABSTAND);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void abholenDerEmails() {
		logger.open(); //�ffnet Log Datei
		for (EmailKonto emailkonto : konten) {
			try {
			//Stellt eine Verbindung zum Server her
			verbindenZuInternetPOP3(emailkonto.getServerAddr(), emailkonto.getPort());
				
			//Versucht sich mit Benutzername und sein Passwort zu authetifizieren
			try{
				while (!authentifiziere(emailkonto));
			}catch(NullPointerException ne){
				 logger.log("Server: Der Dienstanbieter hat Ihren Account gesperrt");
				 continue; //versucht n�chsten Account
			}
			
			//Transaktion mit Server
			int neueMails = getAnzahlderNachrichten();
			for (int i = 1; i <= neueMails; i++) {
				send("RETR " + i);   //RETR i holt die i-te E-Mail vom E-Mail-Server.
				getAntwortVomServer(); //Log Eintrag
				EMail.erzeugeEmailDatei(leseEmail());
				send("DELE " + i);  //löscht die i-te E-Mail am E-Mail-Server.
				getAntwortVomServer();
				}
			//Ende der Verbindung
			send("QUIT");
			schliesseVerbindung();
			}	catch (IOException e) {
				e.printStackTrace();
			}
			}
		logger.close(); //Schlie�t Log Datei
		
	}
	
	private boolean authentifiziere(EmailKonto konto) throws IOException, NullPointerException {
			 send("USER " + konto.getBenutzername());
			 String antwortVomServer = getAntwortVomServer();
			 if (antwortVomServer.startsWith("-ERR")) return false;
			 logger.log(antwortVomServer);
				
			 send("PASS " + konto.getPasswort());
			 antwortVomServer = getAntwortVomServer();
			 if (antwortVomServer.startsWith("-ERR")) return false;
			logger.log(antwortVomServer);
			 return true;

	}
	
	private void send(String befehl) throws IOException {
			writer.write(befehl + "\r\n");
			writer.flush();
			logger.log("Client "+befehl);
	}
	
	private int getAnzahlderNachrichten() throws IOException {
		send("STAT");  //liefert den Status der Mailbox, u. a. die Anzahl aller E-Mails im Postfach und deren Gesamtgr��e (in Byte).
		return Integer.parseInt(getAntwortVomServer().split(" ")[1]);  //Erste Feld Status(+OK oder -Err), zweite Feld Anzahl der Mails, dritte Gr��e aller Mails
	}
	
	private String getAntwortVomServer() throws IOException{
		String antwort = reader.readLine();
		logger.log("Server: " +antwort);
		return antwort;
	}
	
	private String leseEmail() throws IOException{
		 
		String line = reader.readLine();
		byte[] bytes = line.getBytes();
		Charset.forName("UTF-8").decode(ByteBuffer.wrap(bytes));
		StringBuilder nachricht = new StringBuilder(line);
		line = bytes.toString();
		while(!line.equals(".")) {
			line = bytes.toString();
			nachricht.append(line);
			nachricht.append("\r\n");
			line = reader.readLine();
		}
		return nachricht.toString();
	}
	
}
