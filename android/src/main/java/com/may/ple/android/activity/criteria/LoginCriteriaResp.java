package com.may.ple.android.activity.criteria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginCriteriaResp extends CommonCriteriaResp {
	public PrincipalDetail principal = new PrincipalDetail();
	
	public LoginCriteriaResp() {}
	
	public LoginCriteriaResp(Integer statusCode) {
		this.statusCode = statusCode;
	}

}
