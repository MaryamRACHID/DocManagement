package com.structapp.eagle.mailsNotification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMailHelper {

    Session newSession = null;
    MimeMessage mimeMessage = null;
   /* public static void main(String args[]) throws AddressException, MessagingException, IOException
    {
        SendMailHelper mail = new SendMailHelper();
        mail.setupServerProperties();
        mail.draftEmail();
        mail.sendEmail();
    }*/

    public void sendEmail() throws MessagingException {
        String fromUser = "egce.notification@gmail.com";  //Enter sender email id
        String fromUserPassword = "bfkzgasujtcbchwj";  //xldkbtlgldfblgmk bfkzgasujtcbchwjEnter sender gmail password , this will be authenticated by gmail smtp server
        String emailHost = "smtp.gmail.com";
        Transport transport = newSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserPassword);
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();
        System.out.println("Email successfully sent!!!");
    }

    public MimeMessage draftEmail(ArrayList<String> emails, String titre, String bureau, String service, String Date, int nbrDays) throws AddressException, MessagingException, IOException {
        //String[] emailReceipients = {"maryamrachidart@gmail.com"};  //Enter list of email recepients
        String[] emailReceipients = new String[emails.size()];
        emailReceipients = emails.toArray(emailReceipients);
        String emailSubject = "Date d'expiration du document : "+titre;
        String emailBody = "<h3>Ce mail est envoyé automatiquement<br>Nous vous informons que le document: "
                +titre+ "<br>Accordé par le Bureau : "+bureau+"<br>Liée au Service : "+service+
                "<br>va s'expirer prochainement !<br>La date d'expiration est: "+Date+"</h3>";
        mimeMessage = new MimeMessage(newSession);

        for (int i =0 ;i<emailReceipients.length;i++)
        {
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailReceipients[i]));
        }
        mimeMessage.setSubject(emailSubject);

        // CREATE MIMEMESSAGE
        // CREATE MESSAGE BODY PARTS
        // CREATE MESSAGE MULTIPART
        // ADD MESSAGE BODY PARTS ----> MULTIPART
        // FINALLY ADD MULTIPART TO MESSAGECONTENT i.e. mimeMessage object


        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(emailBody,"text/html; charset=utf-8");
        MimeMultipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(bodyPart);
        mimeMessage.setContent(multiPart);
        return mimeMessage;
    }

    public void setupServerProperties() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.port", "465");
        newSession = Session.getDefaultInstance(properties,null);

    }
}
