package com.wso2telco.dep.tpservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TLSMailConfigDTO {
	
	@JsonProperty
	private  String port="587";

	@JsonProperty
	private  String username;

	@JsonProperty
	private  String password;

	@JsonProperty
	private  boolean isStarttlsEnable=true;
	
	@JsonProperty
	private  String host ;
	
	@JsonProperty
	private  boolean isAuth=true;

	@JsonProperty
	private  String from;
	
	
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isStarttlsEnable() {
		return isStarttlsEnable;
	}

	public void setStarttlsEnable(boolean isStarttlsEnable) {
		this.isStarttlsEnable = isStarttlsEnable;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public boolean isAuth() {
		return isAuth;
	}

	public void setAuth(boolean isAuth) {
		this.isAuth = isAuth;
	}
	
	
	
	
}
