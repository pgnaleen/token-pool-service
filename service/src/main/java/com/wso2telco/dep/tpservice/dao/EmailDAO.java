package com.wso2telco.dep.tpservice.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wso2telco.dep.tpservice.model.EmailDTO;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wso2telco.dep.tpservice.util.Constants.Tables;


/**
 * Created by wso2telco(bmla) on 1/31/17.
 */
public class EmailDAO {

    private static Logger log = LoggerFactory.getLogger(EmailDAO.class);

    public EmailDTO getEmailAddress(String tsxwhodid)
    {
        boolean flag = false;

        EmailDTO email = null;
        DBI dbi = JDBIUtil.getInstance();
        Handle h = dbi.open();

        try
        {
            StringBuilder build = new StringBuilder();
            build.append("SELECT * FROM ").append(Tables.TABLE_TSTEMAIL.toString()).append(" ");
            build.append("WHERE tsxwhodid = :tsxwhodid");
            Map<String, Object> resultEmail = h.createQuery(build.toString())
                    .bind("tsxwhodid", tsxwhodid)
                    .first();

            email = new EmailDTO();

            email = getEmailDTOFromResultsMap(resultEmail);

            if(email == null)
            {
                throw new NullPointerException();
            }

        }
        catch(Exception ex)
        {
            log.debug("EmailDAO","getEmailAddress(Id)",ex.getMessage());
        }

        return email;
    }


    private EmailDTO getEmailDTOFromResultsMap(Map<String, Object> resultsMap)
    {
        EmailDTO emaildto = null;
        if(resultsMap != null)
        {
            emaildto = new EmailDTO();
            int emailId = (Integer) resultsMap.get("idtstemail");
            int whoId = (Integer) resultsMap.get("tsxwhodid");
            String emailAddress = (String) resultsMap.get("tstmailaddr");
            emaildto.setEmailAddress(emailAddress);
            emaildto.setWhoId(whoId);
            emaildto.setEmailId(emailId);

        }

        return   emaildto;



    }



}