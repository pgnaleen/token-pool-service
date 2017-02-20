package com.wso2telco.dep.tpservice.dao;

import com.wso2telco.dep.tpservice.model.EmailDTO;
import com.wso2telco.dep.tpservice.model.RetryConnectionDTO;
import com.wso2telco.dep.tpservice.util.Constants;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by wso2telco(Bmla) on 2/8/17.
 */
public class RetryConnectionDAO {

    protected RetryConnectionDTO retryConDTO;
    protected RetryConnectionDTO retryConnect;
    private static Logger log = LoggerFactory.getLogger(RetryConnectionDAO.class);


    public RetryConnectionDTO getRetryDetails(String tsxwhodid)
    {
        DBI dbi = JDBIUtil.getInstance();
        Handle h = dbi.open();


        try
        {
            StringBuilder build = new StringBuilder();
            build.append("SELECT * FROM ").append(Constants.Tables.TABLE_TSXWHO.toString()).append(" ");
            build.append("WHERE tsxwhodid = :tsxwhodid");
            Map<String, Object> resultEmail = h.createQuery(build.toString())
                    .bind("tsxwhodid", tsxwhodid)
                    .first();
             retryConnect = new RetryConnectionDTO();
             retryConnect = getRetryConDTOFrmResultmap(resultEmail);

            if(retryConnect == null)
            {
                throw new NullPointerException();
            }

        }
        catch(Exception ex)
        {
            log.debug("EmailDAO","getEmailAddress(Id)",ex.getMessage());
        }



        return  retryConnect;
    }


    private RetryConnectionDTO getRetryConDTOFrmResultmap(Map<String, Object> resultsMapRetry)
    {
        if(resultsMapRetry != null)
        {
            retryConDTO = new RetryConnectionDTO();
            int retryAttmptCount = (Integer) resultsMapRetry.get("reattmptcount");
            int retryMax = (Integer) resultsMapRetry.get("retrymax");
            int retryDelay = (Integer) resultsMapRetry.get("retrydelay");
            String tokenUrl = (String) resultsMapRetry.get("tokenurl");
            retryConDTO.setRetryAttemptCount(retryAttmptCount);
            retryConDTO.setRetryMax(retryMax);
            retryConDTO.setRetryDelay(retryDelay);
            retryConDTO.setTokenUrl(tokenUrl);
        }
        return retryConDTO;
    }






}
