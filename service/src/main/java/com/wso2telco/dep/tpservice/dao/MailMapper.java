package com.wso2telco.dep.tpservice.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.wso2telco.dep.tpservice.model.EmailDTO;

class MailMapper implements ResultSetMapper<EmailDTO> {

	@Override
	public EmailDTO map(int index, ResultSet r, StatementContext ctx) throws SQLException {
	

		EmailDTO  emaildto = new EmailDTO();
        int emailId = r.getInt("idtstemail");
        int whoId =  r.getInt("tsxwhodid");
        String emailAddress = r.getString("tstmailaddr");
        emaildto.setEmailAddress(emailAddress);
        emaildto.setWhoId(whoId);
        emaildto.setEmailId(emailId);
        
		return emaildto;
	}

}
