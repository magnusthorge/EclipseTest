package Praktikum2;

public class EmailKonto {

	private String benutzername;
	private String passwort;
	private String serverAddr;
	private int port;
	
	public EmailKonto(String benutzername, String passwort, String serverAddr, int port) {
		this.benutzername = benutzername;
		this.passwort = passwort;
		this.serverAddr = serverAddr;
		this.port = port;
	}
	
	public String getBenutzername() {
		return this.benutzername;
	}
	
	public String getPasswort() {
		return this.passwort;
	}
	
	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public int getPort() {
		return port;
	}
	
	
}
