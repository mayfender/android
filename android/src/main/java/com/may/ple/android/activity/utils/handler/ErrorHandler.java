package com.may.ple.android.activity.utils.handler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.may.ple.android.activity.FragmentTabsPager;
import com.may.ple.android.activity.LoginActivity;
import com.may.ple.android.activity.R;
import com.may.ple.android.activity.criteria.CommonCriteriaResp;

public class ErrorHandler {
	private Context context;
	
	public ErrorHandler(Context context) {
		this.context = context;
	}

	public void handler(CommonCriteriaResp resp) {
		String msg = "";
		
		if(resp == null) {
			msg = "Response is null";
		}else if(resp.statusCode == 5000) {
			msg = "ไม่สามารถเชื่อมต่อกับข้อมูลกลางได้";
		}else if(resp.statusCode == 401) {
			if(context instanceof LoginActivity) {
				Toast.makeText(context, "ข้อมูลไม่ถูกต้อง กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();				
			} else {
				new AlertDialog.Builder(context)
	  	        .setIcon(android.R.drawable.ic_dialog_alert)
	  	        .setTitle(R.string.session_timeout_title)
	  	        .setMessage(R.string.session_timeout_msg)
	  	        .setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {

	  	            @Override
	  	            public void onClick(DialogInterface dialog, int which) {
	  	            	Intent intent = new Intent(context, FragmentTabsPager.class);
	  					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
	  			    	context.startActivity(intent);
	  	            }
	  	        })
	  	        .show();
				return;
			}
		}else if(resp.statusCode == 404) {
			Toast.makeText(context, "Not Found Server", Toast.LENGTH_SHORT).show();
			msg = "Not Found Server";
		}else if(resp.statusCode == 1000) {
			msg = "Center Error";
		}else if(resp.statusCode == 1001) {
			msg = "Internal Error";
		}else if(resp.statusCode == 3000) {
			msg = "ไม่พบข้อมูล";
		}else{
			msg = "Error code: " + resp.statusCode;
		}		
		
		
		new AlertDialog.Builder(context)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Error!!!")
	        .setMessage(msg)
	        .setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	
	            }
	        }).show();
		
		
	}
	
}
