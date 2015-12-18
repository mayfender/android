package com.may.ple.android.activity.criteria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrincipalDetail {
	public Boolean authenticated;
	public String name;
}
