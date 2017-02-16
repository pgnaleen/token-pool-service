package com.wso2telco.dep.tpservice.manager;

import com.wso2telco.dep.tpservice.dao.RetryConnectionDAO;
import com.wso2telco.dep.tpservice.dao.WhoDAO;
import com.wso2telco.dep.tpservice.model.RetryConnectionDTO;
import com.wso2telco.dep.tpservice.model.WhoDTO;
import org.quartz.*;
import org.quartz.Calendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

/**
 * Created by wso2telco(bmla) on 1/27/17.
 */
public class ConnectionManager {
    private static Logger log = LoggerFactory.getLogger(ConnectionManager.class);

    RetryConnectionDAO retryDAO;
    RetryConnectionDTO retryDTO;
    protected  WhoDAO whoDao;
    protected WhoDTO whoDto;
    protected  String whoId;
   static int retryAttmpCount = 0;



    HttpsURLConnection connection = null;
 /*   public boolean reConnectivity(String whoId)
    {

        log.error("entering Reconnectivity(1)");
        boolean flag = false;
         retryDAO = new RetryConnectionDAO();
         retryDTO = new RetryConnectionDTO();
        retryDTO = retryDAO.getRetryDetails(whoId);
         retryAttmpCount = retryDTO.getRetryAttemptCount();
        int retryMax = retryDTO.getRetryMax();
        log.error("entering Reconnectivity(2)");
        int retryDelay = retryDTO.getRetryDelay();
        JobDetail connectJob = newJob(ConnectionManager.class)
                .withIdentity("Connection", "retryGrp")
                .build();
        Trigger conTrigger = newTrigger()
                .withIdentity("conTrigger", "retryGrp")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(retryDelay)
                        .repeatForever())
                        .build();
        Properties prop = new Properties();
        prop.put("org.quartz.scheduler.rmi.export", "true");
        prop.put("org.quartz.scheduler.rmi.createRegistry", "true");
        prop.put("org.quartz.scheduler.rmi.registryHost", "localhost");
        prop.put("org.quartz.scheduler.rmi.registryPort", "1099");
        prop.put("org.quartz.dataSource.quartzDataSource.driver", "com.mysql.jdbc.Driver");
        prop.put("org.quartz.dataSource.quartzDataSource.URL", "jdbc:mysql://localhost:6603/quartz");
        prop.put("org.quartz.dataSource.quartzDataSource.user", "root");

        prop.put("org.quartz.dataSource.quartzDataSource.password", "root");
        prop.put("org.quartz.dataSource.quartzDataSource.maxConnections", "30");
        prop.put("org.quartz.threadPool.threadCount", "1");
        log.error("entering Reconnectivity(3)");


        setQuartzConfig();
        if(retryAttmpCount < retryMax && connection==null)
        {
            Scheduler scheduler
            retryAttmpCount = retryAttmpCount + 1;

            try {
                log.error("entering Reconnectivity(4)");
                scheduler.scheduleJob(connectJob, conTrigger);



            } catch (SchedulerException e) {
                log.error("Scheduler not running","reConnectivity",e.getMessage());
            }
        }


        return flag;
    }*/

    public int returnRetryAttmpt()
    {
        return retryAttmpCount;
    }



   /* public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {


        retryDTO = retryDAO.getRetryDetails(whoId);
       // String tokenUrl = retryDTO.getTokenUrl();
        String tokenUrl = "http://172.17.42.1:9763/store/";
        URL url = null;
        try {
            url = new URL(tokenUrl);
            connection = (HttpsURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            log.error("Wrong URL","execute",e.getMessage());
        } catch (IOException e) {
            log.error("ConnectionManager , reConnectivity(), ", e);
        }

    }*/

 /*   public void setQuartzConfig()
    {
        try {
            Properties prop = new Properties();



            //Quartz Server Properties
            prop.put("org.quartz.scheduler.rmi.export", "true");
            prop.put("org.quartz.scheduler.rmi.createRegistry", "true");
            prop.put("org.quartz.scheduler.rmi.registryHost", "localhost");
            prop.put("org.quartz.scheduler.rmi.registryPort", "1099");
            prop.put("quartz.scheduler.instanceName", "ServerScheduler");
            prop.put("org.quartz.scheduler.instanceId", "AUTO");
            prop.put("org.quartz.scheduler.skipUpdateCheck", "true");
            prop.put("org.quartz.scheduler.instanceId", "NON_CLUSTERED");
            prop.put("org.quartz.scheduler.jobFactory.class", "org.quartz.simpl.SimpleJobFactory");
            prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
            prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
            prop.put("org.quartz.jobStore.dataSource", "quartzDataSource");
            prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
            prop.put("org.quartz.jobStore.isClustered", "true");

            //MYSQL DATABASE CONFIGURATION
            prop.put("org.quartz.dataSource.quartzDataSource.driver", "com.mysql.jdbc.Driver");
            prop.put("org.quartz.dataSource.quartzDataSource.URL", "jdbc:mysql://localhost:6603/quartz");
            prop.put("org.quartz.dataSource.quartzDataSource.user", "root");

            prop.put("org.quartz.dataSource.quartzDataSource.password", "root");
            prop.put("org.quartz.dataSource.quartzDataSource.maxConnections", "30");
            prop.put("org.quartz.threadPool.threadCount", "2");

            SchedulerFactory stdSchedulerFactory = new StdSchedulerFactory(prop);
            Scheduler scheduler = stdSchedulerFactory.getScheduler();
            scheduler.start();

        } catch (SchedulerException e) {
            e.printStackTrace();
        }



    }*/
  /*  public boolean reConnectivity(String tokenUrl,String whoId)
    {
        boolean flag = false;
        retryDTO = new RetryConnectionDTO();
        retryDAO = new RetryConnectionDAO();

        retryDTO = retryDAO.getRetryDetails(whoId);
        retryAttmpCount = retryDTO.getRetryAttemptCount();
        int retryMax = retryDTO.getRetryMax();
        int retryDelay = retryDTO.getRetryDelay();

        try {
            URL url = new URL(tokenUrl);
            connection = (HttpsURLConnection) url.openConnection();
            while (retryAttmpCount < retryMax && connection ==null)
            {

               retryAttmpCount +=  1;
               reConnectivity(tokenUrl,whoId);
            }

            if(connection != null)
            {
                flag = true;
            }
            else
            {
                flag = false;
            }
        }
        catch (IOException e)
        {
            log.error("ConnectionManager , reConnectivity(), ", e);
            // throw new TokenException(TokenException.TokenError.CONNECTION_LOSS);
        }
        return flag;
    }*/
}
