package Praktikum2;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Logger {

	String fileName;
	FileWriter fw;
	SimpleDateFormat sdf;
	boolean debugMode;
	
	public Logger(String fileName) {
		this.fileName = fileName;
		try { //Erstellt Logdatei
			this.fw = new FileWriter("log/"+this.fileName+".log", true);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss"); 
	}
	
	public void log(String msg) {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		String logString = sdf.format(time) + ": " + msg + "\r\n";
		try {
			this.fw.write(logString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (this.debugMode) System.out.print(logString);
	}
	
	public void setDebug(boolean debugMode) {
		this.debugMode = debugMode;
	}
	
	public boolean isDebug() {
		return this.debugMode;
	}
	
	public void open() {
		try {
			this.fw = new FileWriter("log/"+this.fileName+".log", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
