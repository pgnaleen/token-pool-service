package com.wso2telco.dep.tpservice.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by wso2telco on 2/15/17.
 */
public interface PersistableWho {


    @SqlUpdate(" UPDATE tsxwho  SET  reattmptcount = 0   WHERE   tsxwhodid = :tsxwhodid")
    public void resetReTryAttemts( @Bind("tsxwhodid")int tsxwhodid ) ;
}
