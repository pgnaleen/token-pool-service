package com.wso2telco.dep.tpservice.model;

/**
 * Created by wso2telco(Bmla) on 2/8/17.
 */
public class RetryConnectionDTO {

    private  int retryAttemptCount ;
    private  int retryMax;
    private  int retryDelay;
    private  String tokenUrl;

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = this.tokenUrl;
    }



    public int getRetryAttemptCount() {
        return retryAttemptCount;
    }

    public void setRetryAttemptCount(int retryAttemptCount) {
        this.retryAttemptCount = retryAttemptCount;
    }

    public int getRetryMax() {
        return retryMax;
    }

    public void setRetryMax(int retryMax) {
        this.retryMax = retryMax;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }
}
