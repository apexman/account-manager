package com.maksimov.accountManager.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Service
public class EmailService {
    private Properties prop = new Properties();
    private Session session;

    public EmailService() {
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.port", "587");

        session = Session.getInstance(prop, null);
    }

    public void sendEmail(String messageText) throws MessagingException {
        MimeMessage newMessage = getNewMessage(messageText);
        Transport transport = getTransport();
        transport.sendMessage(newMessage, newMessage.getAllRecipients());
        transport.close();
    }

    private MimeMessage getNewMessage(String messageText) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("from@gmail.com"));

        message.setSubject("Mail Subject");
        message.setContent(messageText, "text/html");

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(messageText, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        return message;
    }

    private Transport getTransport() throws MessagingException {
        Transport transport = session.getTransport("smtp");
        transport.connect("smtp.gmail.com", "account@gmail.com", "passw0rd");
        return transport;
    }
}
