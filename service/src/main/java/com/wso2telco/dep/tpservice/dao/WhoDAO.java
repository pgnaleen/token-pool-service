/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wso2telco.dep.tpservice.dao;

import com.wso2telco.dep.tpservice.model.EmailDTO;
import com.wso2telco.dep.tpservice.model.WhoDTO;
import com.wso2telco.dep.tpservice.util.Constants.Tables;
import com.wso2telco.dep.tpservice.util.exception.BusinessException;
import com.wso2telco.dep.tpservice.util.exception.GenaralError;
import com.wso2telco.dep.tpservice.util.exception.TokenException;
import com.wso2telco.dep.tpservice.util.exception.TokenException.TokenError;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhoDAO {

	private static Logger log = LoggerFactory.getLogger(WhoDAO.class);

	public static int retryAttmptCount = 0;

    public static void setRetryAttmptCount(int retryAttmptCount) {
        WhoDAO.retryAttmptCount = retryAttmptCount;
    }

	public ArrayList<WhoDTO> getAllOwners() throws SQLException {
		ArrayList<WhoDTO> ownersList = new ArrayList<WhoDTO>();
		DBI dbi = JDBIUtil.getInstance();
		Handle h = dbi.open();
		try {
			
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM ").append(Tables.TABLE_TSXWHO.toString()).append(" ");
			sb.append("WHERE isvalid = 1 ");
			sb.append("ORDER BY ownerid ");
	
			List<Map<String, Object>> resultSet = h.select(sb.toString());
	
			for (int i = 0; i < resultSet.size(); i++) {
				WhoDTO whoDTO = getWhoDTOFromResultsMap(resultSet.get(i));
				ownersList.add(whoDTO);
			}
		} catch (Exception e) {
			log.error("getAllOwners() failed ", e);
			throw new SQLException("Could not get all valid owners");
		} finally {
			h.close();
		}
		return ownersList;
	}

	private WhoDTO getWhoDTOFromResultsMap(Map<String, Object> resultsMap) {
		WhoDTO whoDTO = null;
		if (resultsMap != null) {
			whoDTO = new WhoDTO();
			int id = (Integer)resultsMap.get("tsxwhodid");
			String ownerId = (String)resultsMap.get("ownerid");
			String tokenUrl = (String)resultsMap.get("tokenurl");
			long defaultConnectionResetTime = (Long)resultsMap.get("defaultconnectionresettime");
			boolean isValid = (Boolean)resultsMap.get("isvalid");
			Timestamp createdDate = (Timestamp)resultsMap.get("createddate");
			int uc = (Integer)resultsMap.get("uc");
            int retryAttmpt = (Integer)resultsMap.get("reattmptcount");
            int retryMax = (Integer)resultsMap.get("retrymax");
            int retryDelay = (Integer) resultsMap.get("retrydelay");

			whoDTO.setId(id);
			whoDTO.setOwnerId(ownerId);
			whoDTO.setTokenUrl(tokenUrl);
			whoDTO.setDefaultConnectionRestTime(defaultConnectionResetTime);
			whoDTO.setValid(isValid);
			whoDTO.setCreatedDate(createdDate.getTime());
			whoDTO.setUc(uc);
			whoDTO.setRetryAttmpt(retryAttmpt);
			whoDTO.setMaxRetryCoutn(retryMax);
			whoDTO.setretryDelay(retryDelay);
		} else {
			//resultsMap is null
		}
		return whoDTO;
	}

	public int getRetryAttempt(final String ownerid) {
	return  0;
	}

	public int incrimentRetryAttempt(final int ownerid) {
		DBI dbi = JDBIUtil.getInstance();
		int incrementRetryAttempt = retryAttmptCount +1;
		retryAttmptCount = incrementRetryAttempt;
		PersistableWho persistableWho = dbi.open(PersistableWho.class);
		persistableWho.incrementReTryAttempts(ownerid, incrementRetryAttempt);
		return incrementRetryAttempt;
	}

	public WhoDTO getOwner(String ownerid) {

		WhoDTO returnWhoDto = null;
		DBI dbi = JDBIUtil.getInstance();
		Handle h = dbi.open();
		
		try {

			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM ").append(Tables.TABLE_TSXWHO.toString()).append(" ");
			sb.append("WHERE ownerid = :ownerid");

			Map<String, Object> resultOwner = h.createQuery(sb.toString())
					.bind("ownerid", ownerid)
					.first();
			if (resultOwner==null){
				throw new TokenException(TokenError.NO_VALID_WHO);
			}
			returnWhoDto = getWhoDTOFromResultsMap(resultOwner);

		} catch (TokenException e) {
			log.error("getOwner() failed ", e.getMessage());
		} finally {
			h.close();
		}
		return returnWhoDto;
	}
	
	
	
	 public List<EmailDTO> getEmailAddress(final Integer tsxwhodid) throws BusinessException{

	        List<EmailDTO> emailList  = null;
	        DBI dbi = JDBIUtil.getInstance();
	        try {
	        	
	        	PersistableWho h = dbi.open(PersistableWho.class);
	        	emailList = h.loadSenderList(tsxwhodid);
	        	

	        } catch(Exception ex)  {
	        	ex.printStackTrace();
	        	
	           throw new TokenException(GenaralError.INTERNAL_SERVER_ERROR);
	        }

	        return emailList;
	    }
	 
}
	
	
	
	

