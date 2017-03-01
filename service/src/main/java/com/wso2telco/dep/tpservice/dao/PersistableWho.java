package com.wso2telco.dep.tpservice.dao;

import com.wso2telco.dep.tpservice.model.EmailDTO;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.sql.SQLException;
import java.util.List;

public interface PersistableWho {


    @SqlUpdate(" UPDATE tsxwho  SET  reattmptcount = 0   WHERE   tsxwhodid = :tsxwhodid")
    public void resetReTryAttemts( @Bind("tsxwhodid")int tsxwhodid ) ;

    @SqlUpdate(" UPDATE tsxwho  SET  reattmptcount =:reattmptcount   WHERE   tsxwhodid = :tsxwhodid")
    public void incrementReTryAttempts(@Bind("tsxwhodid")int tsxwhodid, @Bind("reattmptcount") int reattmptcount ) ;

    @SqlQuery("SELECT idtstemail,tsxwhodid,tstmailaddr from tstemail where tsxwhodid = :tsxwhodid")
    @Mapper(MailMapper.class)
    List<EmailDTO> loadSenderList(@Bind("tsxwhodid") int ownerWhoDid)throws SQLException;
    void close();
}
