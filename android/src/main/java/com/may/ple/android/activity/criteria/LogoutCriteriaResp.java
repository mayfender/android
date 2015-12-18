package com.may.ple.android.activity.criteria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogoutCriteriaResp extends CommonCriteriaResp {
	
	public LogoutCriteriaResp() {}
	
	public LogoutCriteriaResp(Integer statusCode) {
		this.statusCode = statusCode;
	}

}
