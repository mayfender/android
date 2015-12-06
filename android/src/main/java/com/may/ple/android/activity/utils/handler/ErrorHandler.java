package com.may.ple.android.activity.utils.handler;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.may.ple.android.activity.LoginActivity;
import com.may.ple.android.activity.criteria.CommonCriteriaResp;

public class ErrorHandler {
	private Context context;
	
	public ErrorHandler(Context context) {
		this.context = context;
	}

	public void handler(CommonCriteriaResp resp) {
		if(resp == null) {
			Toast.makeText(context, "Response is null", Toast.LENGTH_SHORT).show();
		}else if(resp.statusCode == 5000) {
			Toast.makeText(context, "ไม่สามารถเชื่อมต่อกับข้อมูลกลางได้", Toast.LENGTH_SHORT).show();
		}else if(resp.statusCode == 401) {
			if(context instanceof LoginActivity) {
				Toast.makeText(context, "ข้อมูลไม่ถูกต้อง กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();				
			} else {
				Toast.makeText(context, "Session Timeout", Toast.LENGTH_SHORT).show();	
				
				Intent intent = new Intent(context, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		    	context.startActivity(intent);
			}
		}else if(resp.statusCode == 404) {
			Toast.makeText(context, "Not Found Server", Toast.LENGTH_SHORT).show();
		}else if(resp.statusCode == 1000) {
			Toast.makeText(context, "Parking Center Error", Toast.LENGTH_SHORT).show();
		}else if(resp.statusCode == 3000) {
			Toast.makeText(context, "ไม่พบข้อมูล", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(context, "Error code: " + resp.statusCode, Toast.LENGTH_SHORT).show();
		}		
	}
	
}
