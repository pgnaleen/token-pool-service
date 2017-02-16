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

package com.wso2telco.dep.tpservice.pool.alltimefirst;

import com.wso2telco.dep.tpservice.dao.RetryConnectionDAO;
import com.wso2telco.dep.tpservice.dao.WhoDAO;
import com.wso2telco.dep.tpservice.manager.ConnectionManager;
import com.wso2telco.dep.tpservice.model.RetryConnectionDTO;
import com.wso2telco.dep.tpservice.util.Constants;
import com.wso2telco.dep.tpservice.util.exception.ThrowableError;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;

import com.wso2telco.dep.tpservice.model.TokenDTO;
import com.wso2telco.dep.tpservice.model.WhoDTO;
import com.wso2telco.dep.tpservice.pool.TokenReGenarator;
import com.wso2telco.dep.tpservice.util.exception.GenaralError;
import com.wso2telco.dep.tpservice.util.exception.TokenException;

class MasterModeTp extends AbstrController {

	private TokenReGenarator regenarator;
	RetryConnectionDTO retryDTO;
	RetryConnectionDAO retryDAO;
	protected MasterModeTp(WhoDTO whoDTO,TokenDTO tokenDTO) throws TokenException {
		super(whoDTO,tokenDTO);
		log = LoggerFactory.getLogger(MasterModeTp.class);
		this.regenarator = new TokenReGenarator();

	}
	
	public void removeToken(final TokenDTO token) throws TokenException {
			super.removeToken(token);
			log.debug("remove form the DB "+token);
			tokenManager.invalidate(token);
	}
	private TokenDTO newRegenerate() throws  TokenException
	{
		TokenDTO newTokenDTO=null;

            newTokenDTO = regenarator.reGenarate(whoDTO, tokenDTO);
            if(newTokenDTO ==null){
                log.warn("token refresh faild :"+tokenDTO);
                throw new TokenException(GenaralError.INTERNAL_SERVER_ERROR);
            }

			return  newTokenDTO;

	}
	
	@Override
	protected TokenDTO reGenarate( )throws TokenException{
		TokenDTO newTokenDTO=null;
		newTokenDTO = new TokenDTO();
        WhoDAO whodao = new WhoDAO();
		try {
			// generating new token
	        newTokenDTO = regenarator.reGenarate(whoDTO, tokenDTO);

            tokenManager.saveToken(whoDTO, newTokenDTO);
			}
            catch (TokenException e) {
			ThrowableError x = e.getErrorType();
			if(x.getCode().equals(TokenException.TokenError.CONNECTION_LOSS.getCode())){


               // int attCount = whodao.getRetryAttempt(whoDTO.getOwnerId());
				int attCount = whodao.incrimentRetryAttempt(whoDTO.getOwnerId());

                if(attCount >whoDTO.getMaxRetryCoutn()) {
                    log.error("You have reach the maximum retry attempts");
                    boolean flag = sendEmails(Constants.EmailTypes.TYPE_SERVER);
                    throw new TokenException(TokenException.TokenError.REACH_MAX_RETRY_ATTEMPT);

                }

                //do the mailng,
                int number =whoDTO.getId();
                String url = whoDTO.getTokenUrl();

             //   int maxCount = retryDTO.getRetryMax();
                int delay = whoDTO.getRetryDelay();
                attCount +=  1;
                //regenarator.reGenarate(whoDTO, tokenDTO);

                try {
                   // Thread.sleep(delay);
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    throw new TokenException(GenaralError.INTERNAL_SERVER_ERROR);
                }

               // whodao.incrimentRetryAttempt(whoDTO.getOwnerId());

                reGenarate();

                //boolean flag = sendEmails(Constants.EmailTypes.TYPE_CREDENTIALS);

			}else {

               // TokenException.TokenError code = TokenException.TokenError.CONNECTION_LOSS;
              //  log.debug("Enter the catch"+code);
                //do the mailng,

                    boolean flag = sendEmails(Constants.EmailTypes.TYPE_CREDENTIALS);
                throw new TokenException(TokenException.TokenError.INVALID_REFRESH_CREDENTIALS);

			}
		}
			//throw new TokenException(e.getErrorType());
        return newTokenDTO;

		}

	}



