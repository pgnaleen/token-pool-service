package com.wso2telco.dep.tpservice.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

/**
 * @author nuwan
 *
 */
public class ConfigDTO extends Configuration implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8356032431902326005L;
	

	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();
	
	/*@JsonProperty
	private String host;
	
	@JsonProperty
	private int port;*/
	
	@JsonProperty
	private long waitingTimeForToken=200; //times in milliseconds ,default time is 200 ms
	
	@JsonProperty
	private Boolean isMaster;
	
	@JsonProperty
	private int tokenReadretrAttempts = 3; //default 3 times 
	
	@JsonProperty
	private long tokenReadretrAfter=60000; //times in milliseconds,default is set to one minit
	
	@JsonProperty
	private int retryAttempt=10000;
	
	@JsonProperty
	private long refreshWakeUpLeadTime=5000;

	@JsonProperty
	private  String emailUsername="nadiajoy1985";

	@JsonProperty
	private  String emailPassword="1234nadia";

	@JsonProperty
	private  String mailPortType="mail.smtp.port";

	@JsonProperty
	private  String portValue="587";

	@JsonProperty
	private  String authType = "mail.smtp.auth";

	@JsonProperty
	private  String authValue="true";

	@JsonProperty
	private String hostType="mail.smtp.host";

	@JsonProperty
	private  String hostValue="smtp.gmail.com";

	@JsonProperty
	private  String startTlsType = "mail.smtp.starttls.enable";

	@JsonProperty
	private  String startTlsValue="true";

	public String getMailPortType() {
		return mailPortType;
	}

	public String getPortValue() {
		return portValue;
	}

	public String getAuthType() {
		return authType;
	}

	public String getAuthValue() {
		return authValue;
	}

	public String getHostType() {
		return hostType;
	}

	public String getHostValue() {
		return hostValue;
	}

	public String getStartTlsType() {
		return startTlsType;
	}

	public String getStartTlsValue() {
		return startTlsValue;
	}

	public String getSslTrustTpe() {
		return sslTrustTpe;
	}

	public String getSslTrustValue() {
		return sslTrustValue;
	}

	@JsonProperty
	private  String sslTrustTpe="mail.smtp.ssl.trust";

	@JsonProperty
	private  String sslTrustValue="smtp.gmail.com";


	public String getEmailUsername() {
		return emailUsername;
	}

	public void setEmailUsername(String emailUsername) {
		this.emailUsername = emailUsername;
	}

	public String getEmailPassword()
	{
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword)
	{
		this.emailPassword = emailPassword;
	}

	public long getRefreshWakeUpLeadTime() {
		return refreshWakeUpLeadTime;
	}

	public void setRefreshWakeUpLeadTime(long refreshWakeUpLeadTime) {
		this.refreshWakeUpLeadTime = refreshWakeUpLeadTime;
	}

	public int getTokenReadretrAttempts() {
		return tokenReadretrAttempts;
	}

	public void setTokenReadretrAttempts(int tokenReadretrAttempts) {
		if (tokenReadretrAttempts > 3) {
			this.tokenReadretrAttempts = tokenReadretrAttempts;
		}
	}

	public long getTokenReadretrAfter() {
		return tokenReadretrAfter;
	}

	//token read retry mini limit is 1 min
	public void setTokenReadretrAfter(long tokenReadretrAfter) {
		if (tokenReadretrAfter > 60000) {
			this.tokenReadretrAfter = tokenReadretrAfter;
		}
	}
	public int getRetryAttempt(){
		return retryAttempt;
	}
	public Boolean getIsMaster() {
		return isMaster;
	}

	public Boolean isMaster() {
		return isMaster;
	}

	public void setIsMaster(Boolean isMaster) {
		this.isMaster = isMaster;
	}


	public long getWaitingTimeForToken() {
		return waitingTimeForToken;
	}

	public void setWaitingTimeForToken(long waitingTimeForToken) {
		if (waitingTimeForToken>200 &&waitingTimeForToken<20000)
		this.waitingTimeForToken = waitingTimeForToken;
	}

	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

	@Override
	public String toString() {
		return "ConfigDTO [database=" + database + ", waitingTimeForToken=" + waitingTimeForToken + ", isMaster="
				+ isMaster + ", tokenReadretrAttempts=" + tokenReadretrAttempts + ", tokenReadretrAfter="
				+ tokenReadretrAfter + ", retryAttempt=" + retryAttempt + "]";
	}
	
/*	public String getHost(){
		return host;
	}
	
	public int getPort(){
		return port;
	}*/

	
	

}
