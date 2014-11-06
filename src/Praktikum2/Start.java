package Praktikum2;
import Praktikum2.POP3Client;
import Praktikum2.POP3Server;


public class Start {


	public static void main(String[] args) {
		
		
		
		
		POP3Client p = new POP3Client();
		p.addKonto(new EmailKonto("test1@smart-mail.de", "kurix", "pop.smart-mail.de", 110));
		p.addKonto(new EmailKonto("test2@smart-mil.de", "kurix", "pop.smart-mail.de", 110));
		p.addKonto(new EmailKonto("test3@smart-mail.de", "kurix", "pop.smart-mail.de", 110));
		p.start();

		POP3Server server = new POP3Server();
		server.start();

	}
		
}
