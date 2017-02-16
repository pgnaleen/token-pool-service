package com.wso2telco.dep.tpservice.pool.alltimefirst;

import com.wso2telco.dep.tpservice.manager.TokenManager;
import com.wso2telco.dep.tpservice.model.TokenDTO;
import com.wso2telco.dep.tpservice.model.WhoDTO;
import com.wso2telco.dep.tpservice.pool.TokenControllable;
import com.wso2telco.dep.tpservice.util.exception.TokenException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by wso2telco on 2/13/17.
 */
class AbstractSheduler implements Job{
    private static Logger log = LoggerFactory.getLogger(AbstractSheduler.class);

    public WhoDTO whoDTO;
    public  void execute(JobExecutionContext jobExecutionContext){

}

    }





