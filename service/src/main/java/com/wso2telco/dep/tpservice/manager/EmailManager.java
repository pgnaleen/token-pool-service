package com.wso2telco.dep.tpservice.manager;


import javax.mail.*;
import javax.mail.internet.*;
import com.wso2telco.dep.tpservice.conf.ConfigReader;
import com.wso2telco.dep.tpservice.dao.EmailDAO;
import com.wso2telco.dep.tpservice.model.ConfigDTO;
import com.wso2telco.dep.tpservice.model.EmailDTO;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.net.PasswordAuthentication;
import java.security.NoSuchProviderException;
import java.util.Properties;

/**
 * Created by wso2telco(bmla) on 1/31/17.
 */
public class EmailManager {

    protected EmailDAO emailDAO;
    protected EmailDTO emailDTO;
    public ConfigReader configReader ;

    private static Logger log = LoggerFactory.getLogger(TokenManager.class);
    public boolean sendEmail(String id,String subject)
    {

        boolean flag = false;

        configReader = ConfigReader.getInstance();
        ConfigDTO configDTO = configReader.getConfigDTO();
        String from = configDTO.getEmailUsername();
        String password = configDTO.getEmailPassword();
        emailDAO = new EmailDAO();
        emailDTO  = new EmailDTO();
        emailDTO = emailDAO.getEmailAddress(id);
         String emailTo = emailDTO.getEmailAddress();


         if(emailTo == null)
         {
             log.error("null email");
         }
       String emailBody = createMessage(subject);

        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.ssl.trust","smtp.gmail.com");
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(from, password);
                    }
                });
         MimeMessage message = new MimeMessage(session);


        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
            message.setSubject(subject);
            message.setText(emailBody,"utf-8", "html");

            Transport.send(message);
            flag = true;
            System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            flag = false;
            mex.printStackTrace();
        }

        return flag;
    }

    public String createMessage(String subject)
    {

        Velocity.init();
        Template t = Velocity.getTemplate("./src/Email.vm");

        String EmaiType = subject;
        VelocityContext ctx = new VelocityContext();
        ctx.put("EmailType",EmaiType);
         Writer writer = new StringWriter();
         t.merge(ctx, writer); ;
        return String.valueOf(writer);
    }



}