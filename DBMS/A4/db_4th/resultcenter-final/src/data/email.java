package data;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class email
{
 String from ="dadhaniachintan@gmail.com";
        String to = "shreyashkalaria@gmail.com";
        String subject = "this is from java file";
        String message = "message";
        String login = "bookonsms";
        String password = "bookonsell";
        String host="smtp.gmail.com";
        
       public void send(String to,String ps)
        {
        try {
            Properties props = new Properties();
            props.setProperty("mail.host", host);
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.starttls.enable", "true");

            Authenticator auth = new SMTPAuthenticator(login, password);

            Session session1 = Session.getInstance(props, auth);

            MimeMessage msg = new MimeMessage(session1);
            message="Your New password for site bookonsms.com is "+ ps;
            subject="Password Retrival from bookonsms.com";
            msg.setText(message);
            msg.setSubject(subject);
            msg.setFrom(new InternetAddress(from));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            Transport.send(msg);
            System.out.println("Mail Sent");
        	} 
        catch (Exception ex) {
        	System.out.println(ex.toString());
        	ex.printStackTrace();
        } 
        }
         public static void main(String a[])
        {
         email S= new email();
         S.send("shreyashkalaria@gmail.com", "asdfgh");
        }
        private class SMTPAuthenticator extends Authenticator 
        {
        private PasswordAuthentication authentication;
        public SMTPAuthenticator(String login, String password) 
        {
            authentication = new PasswordAuthentication(login, password);
        }

        @Override
		protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    } 
}

      