package com.may.ple.android.activity.service;

import java.nio.charset.Charset;
import java.util.Arrays;
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

import com.may.ple.android.activity.ApplicationScope;
import com.may.ple.android.activity.criteria.CommonCriteriaResp;
import com.may.ple.android.activity.criteria.LoginCriteriaResp;
import com.may.ple.android.activity.utils.constant.SettingKey;

public class CenterService {
	public Object passedParam;
	private Context context;
	private RestTemplate restTemplate;
	private String contextUrl;
	
	public CenterService(Context context) {
		this.context = context;	
		init();
	}
	
	public void reloadSetting() {
		SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(context);
		String ip = setting.getString(SettingKey.parkingCenterIp, "");
		String port = setting.getString(SettingKey.parkingCenterPort, "");
		contextUrl = "http://" + ip + ":" + port + "/backend";
	}
	
	private void init() {
		reloadSetting();
		restTemplate = new RestTemplate();
		ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
		
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
	
	public void login(String username, String password, String uri, final RestfulCallback callback) {
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
					return new LoginCriteriaResp(1001);
				} 
			}
			
			protected void onPostExecute(LoginCriteriaResp result) {
				callback.onComplete(0, result, passedParam);
			};
			
		}.execute(username, password);
	}
	
	public void logout() {
		final String url = this.contextUrl + "/logout";
		
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					HttpHeaders requestHeaders = new HttpHeaders();
					requestHeaders.add("Cookie", "JSESSIONID=" + ApplicationScope.getInstance().jsessionid);
					requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
					HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
					ResponseEntity<String> respEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
					return respEntity.getBody();
				} catch (ResourceAccessException r) {
					return "error";
				} catch (HttpClientErrorException h) {
					return "error";
				} catch (Exception e) {
					return "error";
				} 
			}
			
			protected void onPostExecute(String result) {
				
			};
			
		}.execute();
	}
	
	public <R, T extends CommonCriteriaResp> void send(final int id, final R reqType, final Class<T> respType, String uri, final HttpMethod method, final RestfulCallback callback) {
		final String url = this.contextUrl + uri;
		
		new AsyncTask<String, Void, T>() {

			@Override
			protected T doInBackground(String... params) {
				try {
					
					HttpHeaders requestHeaders = new HttpHeaders();
					requestHeaders.add("Cookie", "JSESSIONID=" + ApplicationScope.getInstance().jsessionid);
					requestHeaders.setAccept(
							Arrays.asList(
									new MediaType("application","json"), 
									new MediaType("text","plain", Charset.forName("UTF-8"))
									)
							);
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
						T t = respType.newInstance();
						t.statusCode = 1001;						
						return t;
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
