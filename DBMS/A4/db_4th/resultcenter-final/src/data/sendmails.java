package data;

import java.util.*; 
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class sendmails
{
		String from ="shreyashkalaria18@gmail.com";
        String to = "shreyashkalaria@gmail.com";
        String subject = "this is from java file";
        String message = "message";
        String login = "shreyashkalaria18@gmail.com";
        String password = "erno090170107044";
        String host = "smtp.gmail.com";
        
       public void send(String to,String ps)
        {
    	   
        try 
        {
            Properties props = new Properties();
            props.setProperty("mail.host", host);
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.starttls.enable", "true");
            Authenticator auth = new SMTPAuthenticator(login, password);
            Session session1 = Session.getInstance(props, auth);
            MimeMessage msg = new MimeMessage(session1);
            message="Your New password for site resultcenter.com is "+ ps;
            subject="Password Retrival from resultcenter.com";
            msg.setText(message);
            msg.setSubject(subject);
            msg.setFrom(new InternetAddress(from));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            
            Transport transport = session1.getTransport("smtp");
            transport.connect(host, login, password);
            msg.saveChanges();
            Transport.send(msg);
            transport.close();
            
            //Transport.send(msg);
            System.out.println("Mail Sent");
        } 
        catch (Exception ex) 
        {
        System.out.println(ex.toString());
        ex.printStackTrace();
        } 
       
        }
 
         public static void main(String a[])
        {
         sendmails S= new sendmails();
            S.send("shreyashkalaria@gmail.com","shreyashkalaria");
        }
   
         private class SMTPAuthenticator extends Authenticator
         {

        	 private PasswordAuthentication authentication;

        	 public SMTPAuthenticator(String login, String password) 
        	 {
        		 authentication = new PasswordAuthentication(login, password);
        	 }
        	 
        	 protected PasswordAuthentication getPasswordAuthentication()
        	 {
        		 return authentication;
        	 }
         } 
         
         
}