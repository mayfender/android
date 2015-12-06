package com.may.ple.android.activity;

import android.app.Application;

public class ApplicationScope extends Application {
	private static ApplicationScope instance = null;
	public String jsessionid;
	public int connTimeout = 10000;
	public int readTimeout = 10000;
	public String deviceId;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public static ApplicationScope getInstance() {
		return instance;
	}

}
