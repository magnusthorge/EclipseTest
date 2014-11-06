package Praktikum2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EMail {
	
	private long UID;
	private String inhalt;
	private long groesseinByte;
	private boolean loeschFlag;
	
	public static final String NACHRICHTENVERZEICHNIS = "mail/";
	
	public EMail(long UID, String inhalt, long groesse) {
		this.UID = UID;
		this.inhalt = inhalt;
		this.groesseinByte = groesse;
		this.loeschFlag = false;
	}
	
	public static void erzeugeEmailDatei(String emailInhalt) {
		try {
			FileWriter fw = new FileWriter(NACHRICHTENVERZEICHNIS + System.nanoTime() % 10000 + ".email");
			
			fw.write(emailInhalt);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static EMail ladeEmailAusDatei(String dateiname) {
		File mailFile = new File(NACHRICHTENVERZEICHNIS + dateiname);
		try {
			FileReader fr = new FileReader(mailFile);
			BufferedReader br = new BufferedReader(fr);
			
			long uid = Long.parseLong(dateiname.substring(0, dateiname.indexOf('.'))); //UID wird aus gesamte EMAIL "gehasht" 
			StringBuilder inhalt = new StringBuilder();
			while(br.ready()) {
				inhalt.append(br.readLine() + "\r\n");
			}
			br.close();
			return new EMail(uid, inhalt.toString(), mailFile.length());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public long getUID() {
		return UID;
	}

	public String getInhalt() {
		return inhalt;
	}

	public long getGroesseInByte() {
		return groesseinByte;
	}

	public boolean markiertZumLoschen() {
		return loeschFlag;
	}

	public void setzeAUfMarkiertZumLoeschen(boolean loeschen) {
		this.loeschFlag = loeschen;
	}
}
