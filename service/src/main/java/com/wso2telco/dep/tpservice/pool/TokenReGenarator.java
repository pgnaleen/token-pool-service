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

package com.wso2telco.dep.tpservice.pool;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.Response.Status;

import com.wso2telco.dep.tpservice.conf.ConfigReader;
import com.wso2telco.dep.tpservice.model.ConfigDTO;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wso2telco.dep.tpservice.model.TokenDTO;
import com.wso2telco.dep.tpservice.model.WhoDTO;
import com.wso2telco.dep.tpservice.util.Constants;
import com.wso2telco.dep.tpservice.util.Constants.AuthMethod;
import com.wso2telco.dep.tpservice.util.exception.GenaralError;
import com.wso2telco.dep.tpservice.util.exception.ThrowableError;
import com.wso2telco.dep.tpservice.util.exception.TokenException;
import com.wso2telco.dep.tpservice.util.exception.TokenException.TokenError;

public class TokenReGenarator {

	private int responseCode;
	private ConfigReader configReader;

	static {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		// Ignore differences between given hostname and certificate hostname
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {
		}
	}

	private static Logger log = LoggerFactory.getLogger(TokenReGenarator.class);

	// regenerate new access token process
	public TokenDTO reGenarate(final WhoDTO who, final TokenDTO oldToken) throws TokenException {

		this.configReader = ConfigReader.getInstance();
		ConfigDTO configDTO = configReader.getConfigDTO();

		List<String> responseCodeList = Arrays.asList(configDTO.getRetryResponseCodes().split("\\s*,\\s*"));


		final String grant_type = "grant_type=refresh_token&refresh_token=";
		TokenDTO token = new TokenDTO();

		// response containing new access & refresh token
		String Strtoken = makeTokenrequest(who.getTokenUrl(), grant_type + oldToken.getRefreshToken(),
				("" + oldToken.getTokenAuth()));


		 if(responseCodeList.contains(Integer.toString(responseCode))) {

			throw new TokenException(new ThrowableError() {

				@Override
				public String getMessage() {
					return TokenException.TokenError.RESPONSE_CODE_ERROR.getMessage();
				}

				@Override
				public String getCode() {
					return TokenException.TokenError.RESPONSE_CODE_ERROR.getCode();
				}
			});

		}

		// validate response message
		if (Strtoken != null && Strtoken.length() > 0) {

			JSONObject jsontoken = new JSONObject(Strtoken);

			if (jsontoken.has("error" )) {
				throw new TokenException(new ThrowableError() {

					@Override
					public String getMessage() {
						return jsontoken.getString("error_description");
					}

					@Override
					public String getCode() {
						return jsontoken.getString("error");
					}
				});

			}

			String newToken = jsontoken.getString("access_token");
			String newRefreshToken = jsontoken.getString("refresh_token");
			Long newTokenValidity = 1000 * jsontoken.getLong("expires_in");

			token.setAccessToken(newToken);
			token.setTokenAuth(oldToken.getTokenAuth());
			token.setRefreshToken(newRefreshToken);
			token.setTokenValidity(newTokenValidity);
			token.setValid(true);
			token.setWhoId(oldToken.getWhoId());
			//Setting parent token id
			token.setParentTokenId(oldToken.getId());

			log.debug("Refresh token re-generation success" +token);

		} else {
			log.warn("unable to re -genarate token" + oldToken);
			throw new TokenException(GenaralError.INTERNAL_SERVER_ERROR);
		}
		return token;

	}

	protected String makeTokenrequest(String tokenurl, String urlParameters, String authheader) throws TokenException {
		StringBuffer retStr = new StringBuffer();
		HttpsURLConnection connection = null;
		InputStream is = null;
		BufferedReader br = null;
		DataOutputStream wr = null;

		log.debug("Token regeneration requestString : tokenUrl=" + tokenurl + " | urlParameters= " + urlParameters + " | authheader= " + authheader);

		// parameter validations
		if ((tokenurl == null || tokenurl.length() <= 0)) {
			log.error("TokenReGenarator , makeTokenrequest()", "Token URL is invalid");
			throw new TokenException(TokenError.NO_VALID_TOKEN_URL);
		} 
		if (urlParameters == null || urlParameters.length() <= 0) {
			log.error("TokenReGenarator , makeTokenrequest()", "Refresh Token is Invalid");
			throw new TokenException(TokenError.NULL_REFRESH_TOKEN);
		} 
		if (authheader == null || authheader.length() <= 0) {
			log.error("TokenReGenarator , makeTokenrequest()", "Authenticator Header is Invalid");
			throw new TokenException(TokenError.INVALID_AUTH_HEADER);
		} 



		try {

			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;
			URL url = new URL(tokenurl);
			connection = (HttpsURLConnection) url.openConnection();

			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod(Constants.URLProperties.URL_METHOD.getValue());
			connection.setRequestProperty(Constants.URLProperties.AUTHORIZATION_GRANT_TYPE.getValue(),
					AuthMethod.BASIC + authheader);
			connection.setRequestProperty(Constants.URLTypes.CONTENT.getType(),
					Constants.URLTypes.CONTENT.getValue());
			connection.setRequestProperty(Constants.URLTypes.ENCODING.getType(),
					Constants.URLTypes.ENCODING.getValue());
			connection.setRequestProperty(Constants.URLProperties.LENGTH.getValue(),
					Integer.toString(postDataLength));
			connection.setUseCaches(false);


			wr = new DataOutputStream(connection.getOutputStream());
			wr.write(postData);
			wr.flush();
			wr.close();

			// filter out invalid http codes
			if ((connection.getResponseCode() == Status.OK.getStatusCode())
					|| (connection.getResponseCode() == Status.CREATED.getStatusCode())) {
				is = connection.getInputStream();
				responseCode = connection.getResponseCode();
			} else {
				is = connection.getErrorStream();
				responseCode = connection.getResponseCode();
			}

			log.info("Response Code = "+responseCode);

			br = new BufferedReader(new InputStreamReader(is));
			String output;
			while ((output = br.readLine()) != null) {
				retStr.append(output);
			}
		}
		catch(ConnectException e)
		{
			log.error("TokenReGenarator , makerequest(), ", e);

			throw new TokenException(TokenError.CONNECTION_LOSS);
		}

		catch (Exception e) {
			log.error("TokenReGenarator , makerequest(), ", e);
			throw new TokenException(TokenError.TOKEN_REGENERATE_FAIL);
		} finally {
			try {
				if(br!=null){

					br.close();

				}
				if(wr!=null){

					wr.close();
				}

			} catch (IOException e) {


			}

			if (connection != null) {
				connection.disconnect();
			}
		}

		return retStr.toString();
	}

}
