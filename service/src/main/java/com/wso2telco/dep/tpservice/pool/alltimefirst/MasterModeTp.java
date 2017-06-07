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
import com.wso2telco.dep.tpservice.manager.EmailManager;
import com.wso2telco.dep.tpservice.model.RetryConnectionDTO;
import com.wso2telco.dep.tpservice.model.TokenDTO;
import com.wso2telco.dep.tpservice.model.WhoDTO;
import com.wso2telco.dep.tpservice.pool.TokenReGenarator;
import com.wso2telco.dep.tpservice.util.exception.BusinessException;
import com.wso2telco.dep.tpservice.util.exception.GenaralError;
import com.wso2telco.dep.tpservice.util.exception.ThrowableError;
import com.wso2telco.dep.tpservice.util.exception.TokenException;
import org.slf4j.LoggerFactory;

class MasterModeTp extends AbstrController {

	private TokenReGenarator regenarator;
	private TokenDTO newlyGeneratedToken;
	RetryConnectionDTO retryDTO;
	RetryConnectionDAO retryDAO;
	private  final String MAIL_BODY_CONNECTION_LOSS = "Token generation failed. Retry attempt :";
	private final String MAIL_SUBJECT_CONNECTION_LOSS = "[Token Generation Failed]- Error occurd while connecting to ";
	
	private  final String MAIL_BODY_INVALID_CREDENTIALS = "Token generation failed.\nInvalid Credentials in Token Auth -  ";
	private final String MAIL_SUBJECT_INVALID_CREDENTIALS = "[Token Generation Failed]- Invalid Credentials in ";
	private final String MAIL_SUBJECT_END_OF_RETRY =  "The reach the maximum retry attempts reached. Make sure the token endpoint is up and running "
		+ "Restart the owners token pool manually.";
	private final String MAIL_SUBJECT_UNEXPECTED_ERROR = "[Token Genaration Failed]- Response Code : ";
	private  final String MAIL_BODY_UNEXPECTED_ERROR = "Token generation failed.\nUnexpected error occurs when token generation in auth token  ";
	
	protected EmailManager manager;
	protected MasterModeTp(WhoDTO whoDTO,TokenDTO tokenDTO) throws TokenException {
		super(whoDTO,tokenDTO);
		log = LoggerFactory.getLogger(MasterModeTp.class);
		this.regenarator = new TokenReGenarator();
		this.manager = EmailManager.getInstance();
	}
	
	public void removeToken(final TokenDTO token) throws TokenException {
			super.removeToken(token);
			log.debug("remove form the DB "+token);
			tokenManager.invalidate(token);
	}
	
	@Override
	protected TokenDTO reGenarate() throws TokenException {
		TokenDTO newTokenDTO = null;
		newTokenDTO = new TokenDTO();
		WhoDAO whodao = new WhoDAO();
		try {
			// generating new token
			newTokenDTO = regenarator.reGenarate(whoDTO, tokenDTO);

			tokenManager.saveToken(whoDTO, newTokenDTO);
			setNewlyGeneratedToken(newTokenDTO);
			
		} catch (TokenException e) {
			ThrowableError x = e.getErrorType();
			if (x.getCode().equals(TokenException.TokenError.CONNECTION_LOSS.getCode()) || x.getCode().equals(TokenException.TokenError.RESPONSE_CODE_ERROR.getCode())) {
				whoDTO.incriseRetryAttmpt();

			 whodao.incrimentRetryAttempt(whoDTO.getId(),whoDTO.getRetryAttmpt());

				try {
					manager.sendConnectionFailNotification(whoDTO,MAIL_SUBJECT_CONNECTION_LOSS + whoDTO.getOwnerId(), MAIL_BODY_CONNECTION_LOSS+whoDTO.getRetryAttmpt(),  e);

				if (whoDTO.getRetryAttmpt() >= whoDTO.getMaxRetryCount()) {
					log.error("You have reach the maximum retry attempts :"+whoDTO);
					manager.sendConnectionFailNotification(whoDTO,MAIL_SUBJECT_CONNECTION_LOSS + whoDTO.getOwnerId(),MAIL_SUBJECT_END_OF_RETRY,  e);
					throw new TokenException(TokenException.TokenError.REACH_MAX_RETRY_ATTEMPT);

				}
				} catch (BusinessException e2) {
					log.error("reGenarate ",e2);
					throw new TokenException(GenaralError.INTERNAL_SERVER_ERROR);
				}
				
				// do the mailng,
				int number = whoDTO.getId();
				String url = whoDTO.getTokenUrl();

				int delay = whoDTO.getRetryDelay();

				try {
					Thread.sleep(delay);

				} catch (InterruptedException e1) {
					log.error("reGenarate ",e1);
					throw new TokenException(GenaralError.INTERNAL_SERVER_ERROR);
				}

				reGenarate();

			} else if(x.getCode().equals("invalid_client")) {

				try {
					manager.sendConnectionFailNotification(whoDTO,MAIL_SUBJECT_INVALID_CREDENTIALS + whoDTO.getOwnerId(), MAIL_BODY_INVALID_CREDENTIALS + tokenDTO.getTokenAuth(),  e);
				} catch (BusinessException e1) {
					log.error("reGenarate ",e1);
					throw new  TokenException(TokenException.TokenError.EMAIL_SENDING_FAIL);
				}
				
				throw new TokenException(TokenException.TokenError.INVALID_REFRESH_CREDENTIALS);

			}else {

				try {
					manager.sendConnectionFailNotification(whoDTO,MAIL_SUBJECT_UNEXPECTED_ERROR +regenarator.getResponseCode() +" in " +whoDTO.getOwnerId(), MAIL_BODY_UNEXPECTED_ERROR + tokenDTO.getTokenAuth(), e);
				} catch (BusinessException e1) {
					log.error("reGenarate ",e1);
					throw new  TokenException(TokenException.TokenError.EMAIL_SENDING_FAIL);
				}

				throw new TokenException(TokenException.TokenError.UNEXPECTED_ERROR);
			}
		}
		// throw new TokenException(e.getErrorType());
		return getNewlyGeneratedToken();

	}

	private TokenDTO getNewlyGeneratedToken() {
		return newlyGeneratedToken;
	}

	private void setNewlyGeneratedToken(TokenDTO newlyGeneratedToken) {
		this.newlyGeneratedToken = newlyGeneratedToken;
	}
}



