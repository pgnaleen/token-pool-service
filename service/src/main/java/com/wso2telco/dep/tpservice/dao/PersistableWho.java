package com.wso2telco.dep.tpservice.dao;

import java.sql.SQLException;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.wso2telco.dep.tpservice.model.EmailDTO;

public interface PersistableWho {


    @SqlUpdate(" UPDATE tsxwho  SET  reattmptcount = 0   WHERE   tsxwhodid = :tsxwhodid")
    public void resetReTryAttemts( @Bind("tsxwhodid")int tsxwhodid ) ;
    
    @SqlQuery("select id, name from tstemail where tsxwhodid = :tsxwhodid")
    @Mapper(MailMapper.class)
    List<EmailDTO> loadSenderList(@Bind("tsxwhodid") int ownerWhoDid)throws SQLException;
    void close();
}
