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
      static int retryAttmpCount = 0;

    public int returnRetryAttmpt()
    {
        return retryAttmpCount;
    }

}
