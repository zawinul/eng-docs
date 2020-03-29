package it.eng.ms.camundademo.utils;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail  {
    private static final Logger LOGGER = Logger.getAnonymousLogger();

    private static final String DEFAULT_SERVER = "smtp.office365.com";
    private static final int DEFAULT_PORT = 587;
    private static final String DEFAULT_LOGIN = "paolo.andrenacci@eng.it";
    private static final String DEFAULT_PASSWORD = "piGreco3.14";
    private static final String DEFAULT_FROM = "paolo.andrenacci@eng.it";

    public static  String server = DEFAULT_SERVER;
    public static  int port = DEFAULT_PORT;
    public static  String login = DEFAULT_LOGIN;
    public static  String password = DEFAULT_PASSWORD;
    public static  String from = DEFAULT_FROM;

    public SendMail() {
    }

    public boolean send(String subject, String content, String to[], String cc[]) {

        try {
            Properties config = new Properties(); 
            config.put("mail.smtp.auth", "true");
            config.put("mail.smtp.starttls.enable", "true");
            config.put("mail.smtp.host", server);
            config.put("mail.smtp.port", port);

            Session session = Session.getInstance(config, new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(login, password);
                }
            });    	

        	Message m = new MimeMessage(session);
            if (to!=null)
            	for(String adr: to)
            		m.addRecipient(RecipientType.TO, new InternetAddress(adr));
            if (cc!=null)
            	for(String adr: cc)
            		m.addRecipient(RecipientType.CC, new InternetAddress(adr));
            m.setFrom(new InternetAddress(from));
            m.setSubject(subject);
            if (content.trim().startsWith("<"))
            	m.setContent(content, "text/html; charset=utf-8");
            else
            	m.setContent(content, "text/plain; charset=utf-8");
            m.setSentDate(new Date());
            Transport.send(m);
            return true;
        } catch (final MessagingException ex) {
            LOGGER.log(Level.WARNING, "Errore nell'invio mail: " + ex.getMessage(), ex);
            return false;
        }
    }


}
