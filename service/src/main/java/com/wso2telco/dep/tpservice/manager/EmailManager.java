package com.wso2telco.dep.tpservice.manager;


import com.wso2telco.dep.tpservice.conf.ConfigReader;
import com.wso2telco.dep.tpservice.dao.WhoDAO;
import com.wso2telco.dep.tpservice.model.EmailDTO;
import com.wso2telco.dep.tpservice.model.TLSMailConfigDTO;
import com.wso2telco.dep.tpservice.model.WhoDTO;
import com.wso2telco.dep.tpservice.util.exception.BusinessException;
import com.wso2telco.dep.tpservice.util.exception.GenaralError;
import com.wso2telco.dep.tpservice.util.exception.TokenException;
import com.wso2telco.dep.tpservice.util.exception.TokenException.TokenError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public  class EmailManager {

    protected EmailDTO emailDTO;
    public ConfigReader configReader ;
    public WhoDAO whoDAO;
    private Session session ;
    private String mailFrom;
    
  private   EmailManager(){
    	TLSMailConfigDTO tLSMailConfigDTO  = configReader.getInstance().getConfigDTO().getTLSMailConfigDTO();
    	mailFrom = tLSMailConfigDTO.getFrom();

    	whoDAO = new WhoDAO();
    	
    	Properties props = new Properties();
    	props.put("mail.smtp.auth", tLSMailConfigDTO.isAuth());
    	props.put("mail.smtp.starttls.enable",tLSMailConfigDTO.isStarttlsEnable());
    	props.put("mail.smtp.host", tLSMailConfigDTO.getHost());
    	props.put("mail.smtp.port", tLSMailConfigDTO.getPort());

    	  session = Session.getInstance(props,
    	  new javax.mail.Authenticator() {
    		protected PasswordAuthentication getPasswordAuthentication() {
    			return new PasswordAuthentication(tLSMailConfigDTO.getUsername(), tLSMailConfigDTO.getPassword());
    		}
    	  });
    }
   
    // Thread local variable containing each thread's ID
    private static final ThreadLocal<EmailManager> threadId =
        new ThreadLocal<EmailManager>() {
            @Override protected EmailManager initialValue() {
                return new EmailManager();
        }
    };

    // Returns the current thread's unique ID, assigning it if necessary
    
	public static EmailManager getInstance(){
		return threadId.get();
	} 
	
	
    private static Logger log = LoggerFactory.getLogger(TokenManager.class);
    
    /**
     * this will send Connection fail  mail to pre-configured recipients 
     * @param tokenOwner
     * @throws BusinessException
     */
    public void sendConnectionFailNotification(WhoDTO tokenOwner,String subject,String msg, TokenException e) throws BusinessException{
    	
    	try {
    		
    		List<InternetAddress> senderList = new ArrayList<>();
    		
    		List<EmailDTO> rowEmailDTOList = whoDAO.getEmailAddress(tokenOwner.getId());
    		
    		if(rowEmailDTOList==null || rowEmailDTOList.isEmpty()){
    			throw new TokenException(TokenError.EMPTY_SENDER_LIST);
    		}
    		
    		for (EmailDTO emailDTO : rowEmailDTOList) {
    			senderList.add(  new InternetAddress(emailDTO.getEmailAddress()) );
			}
    		
    		
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailFrom));
			
			message.setRecipients(Message.RecipientType.TO, senderList.toArray( new InternetAddress [senderList.size()]));
			message.setSubject(subject);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			e.printStackTrace(ps);
			String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
			
			message.setText(msg
				+ "\n\n "+ content);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException eMessagingException) {
			
			throw new TokenException(GenaralError.INTERNAL_SERVER_ERROR);
		}
    	
    	
    }



  /*  public String createMessage(String subject)
    {

        Velocity.init();
        Template t = Velocity.getTemplate("./src/Email.vm");

        String EmaiType = subject;
        VelocityContext ctx = new VelocityContext();
        ctx.put("EmailType",EmaiType);
         Writer writer = new StringWriter();
         t.merge(ctx, writer); ;
        return String.valueOf(writer);
    }*/


 


}