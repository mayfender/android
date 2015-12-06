package com.may.ple.android.activity.service;

import java.util.Collections;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.may.ple.android.activity.ApplicationScope;
import com.may.ple.android.activity.criteria.CommonCriteriaResp;
import com.may.ple.android.activity.criteria.LoginCriteriaResp;
import com.may.ple.android.activity.utils.constant.SettingKey;

public class CenterService {
	public Object passedParam;
	private Context context;
	private RestTemplate restTemplate;
	private RestfulCallback callback;
	private String ip;
	private String port;
	private String contextUrl;
	
	public CenterService(Context context, RestfulCallback callback) {
		this.context = context;	
		this.callback = callback;
		init();
	}
	
	private void init() {
		SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(context);
		ip = setting.getString(SettingKey.parkingCenterIp, "");
		port = setting.getString(SettingKey.parkingCenterPort, "");
		restTemplate = new RestTemplate();
		ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
		contextUrl = "http://" + ip + ":" + port + "/backend";
		
		if (factory instanceof SimpleClientHttpRequestFactory) {
			SimpleClientHttpRequestFactory simpleFactory = (SimpleClientHttpRequestFactory)restTemplate.getRequestFactory();
			simpleFactory.setConnectTimeout(ApplicationScope.getInstance().connTimeout);
			simpleFactory.setReadTimeout(ApplicationScope.getInstance().readTimeout);
		} else if (factory instanceof HttpComponentsClientHttpRequestFactory) {
			HttpComponentsClientHttpRequestFactory httpFactory = (HttpComponentsClientHttpRequestFactory)restTemplate.getRequestFactory();
			httpFactory.setConnectTimeout(ApplicationScope.getInstance().connTimeout);
			httpFactory.setReadTimeout(ApplicationScope.getInstance().readTimeout);
		}
	}
	
	public void login(String username, String password, String uri) {
		final String url = this.contextUrl + uri;
		
		new AsyncTask<String, Void, LoginCriteriaResp>() {

			@Override
			protected LoginCriteriaResp doInBackground(String... params) {
				try {
					HttpAuthentication authHeader = new HttpBasicAuthentication(params[0], params[1]);
					HttpHeaders requestHeaders = new HttpHeaders();
					requestHeaders.setAuthorization(authHeader);
					requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
					HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
					
					ResponseEntity<LoginCriteriaResp> respEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, LoginCriteriaResp.class);
					String cookie = respEntity.getHeaders().get("Set-Cookie").get(0);
					String jsessionid = cookie.split(";")[0];
					ApplicationScope.getInstance().jsessionid = jsessionid.split("=", 2)[1];
					
					return respEntity.getBody();
				} catch (ResourceAccessException r) {					
					return new LoginCriteriaResp(5000);
				} catch (HttpClientErrorException h) {
					return new LoginCriteriaResp(h.getStatusCode().value());
				} catch (Exception e) {
					return new LoginCriteriaResp(1000);
				} 
			}
			
			protected void onPostExecute(LoginCriteriaResp result) {
				callback.onComplete(0, result, passedParam);
			};
			
		}.execute(username, password);
	}
	
	public <R, T extends CommonCriteriaResp> void send(final int id, final R reqType, final Class<T> respType, String uri, final HttpMethod method) {
		final String url = this.contextUrl + uri;
		
		new AsyncTask<String, Void, T>() {

			@Override
			protected T doInBackground(String... params) {
				try {
					
					HttpHeaders requestHeaders = new HttpHeaders();
					requestHeaders.add("Cookie", "JSESSIONID=" + ApplicationScope.getInstance().jsessionid);
					requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
					HttpEntity<?> requestEntity = new HttpEntity<Object>(reqType, requestHeaders);
					ResponseEntity<T> respEntity = restTemplate.exchange(url, method, requestEntity, respType);
					return respEntity.getBody();
				} catch (ResourceAccessException r) {
					try {
						T t = respType.newInstance();
						t.statusCode = 5000;						
						return t;
					} catch (Exception e) {
						return null;
					}
				} catch (HttpClientErrorException h) {
					try {
						T t = respType.newInstance();
						t.statusCode = h.getStatusCode().value();						
						return t;
					} catch (Exception e) {
						return null;
					}
				} catch (Exception e) {
					try {
						Toast.makeText(context, "Gateway App Error: " +e.getMessage(), Toast.LENGTH_SHORT).show();
						return null;
					} catch (Exception e2) {
						return null;
					}
				} 
			}
			
			protected void onPostExecute(T result) {
				callback.onComplete(id, result, passedParam);
			};
			
		}.execute();
	}
	
}
