package RNP2;

import java.io.*;
import java.util.*;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {
	
	private ArrayList<Properties> emailKonten;
	
	public EmailUtil() {
	}
	
	public boolean addKonto(String hostPop, String user, String passwort, String portPop, String hostSmtp, String portSmtp){
		
		final Properties props = new Properties();
		
		// Zum Empfangen
				props.setProperty("mail.pop3.host", hostPop);
				props.setProperty("mail.pop3.user", user);
				props.setProperty("mail.pop3.password", passwort);
				props.setProperty("mail.pop3.port", portPop);
				props.setProperty("mail.pop3.auth", "true");
				props.setProperty("mail.pop3.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");

				// Zum Senden
				props.setProperty("mail.smtp.host", hostSmtp);
				props.setProperty("mail.smtp.auth", "true");
				props.setProperty("mail.smtp.port", portSmtp);
				props.setProperty("mail.smtp.socketFactory.port", portSmtp);
				props.setProperty("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
				props.setProperty("mail.smtp.socketFactory.fallback", "false");
		
				//Erzeugung eines Sessions Objekts
				Session session = Session.getInstance(props, new javax.mail.Authenticator() {

					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(props
								.getProperty("mail.pop3.user"), props
								.getProperty("mail.pop3.password"));
					}
				});
		
				
				try{
					
				    // Recipient's email ID needs to be mentioned.
				      String to = "philippstaib@web.de";

				      // Sender's email ID needs to be mentioned
				      String from = "d.suewolto@gmail.com";
			         // Create a default MimeMessage object.
			         MimeMessage message = new MimeMessage(session);

			         // Set From: header field of the header.
			         message.setFrom(new InternetAddress(from));

			         // Set To: header field of the header.
			         message.addRecipient(Message.RecipientType.TO,
			                                  new InternetAddress(to));

			         // Set Subject: header field
			         message.setSubject("This is the Subject Line!");

			         // Now set the actual message
			         message.setText("This is actual message");

			         // Send message
			         Transport.send(message);
			         return true;
			      }catch (MessagingException mex) {
			         return false;
			      }
			
	}
	public void abholungAllerMails( ArrayList<Properties> emailKonten){
		
	}
	public void loeschenDerAbgeholtenMails( ArrayList<Properties> emailKonten){
		
	}

}
