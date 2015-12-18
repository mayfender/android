package com.may.ple.android.activity;

import org.springframework.http.HttpMethod;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.may.ple.android.activity.criteria.VehicleSaveCriteriaReq;
import com.may.ple.android.activity.criteria.VehicleSaveCriteriaResp;
import com.may.ple.android.activity.dialog.ProgressDialogSpinner;
import com.may.ple.android.activity.service.CenterService;
import com.may.ple.android.activity.service.RestfulCallback;
import com.may.ple.android.activity.utils.constant.SettingKey;
import com.may.ple.android.activity.utils.handler.ErrorHandler;

public class GateInActivity extends SherlockActivity implements OnLongClickListener, RestfulCallback {
	private String licenseNo = "";
	private TextView show;
	private CenterService service;
	private ProgressDialogSpinner spinner;
	private boolean isCheckOut;
	private int lenght = 4;
	private String gateName;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCheckOut = getIntent().getBooleanExtra("isCheckOut", false);
        spinner = new ProgressDialogSpinner(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gate_in);
        
        show = (TextView)findViewById(R.id.show);
        show.setOnLongClickListener(this);
        
        Button delete = (Button)findViewById(R.id.delete);
        delete.setOnLongClickListener(this);
        service = new CenterService(this);
        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(this);
//        gateName = setting.getString(SettingKey.gateName, null);
    }
	
	public void onClick(View view) {
		
		if(view.getId() == R.id.delete) {
			if(licenseNo.length() > 0)
				licenseNo = licenseNo.substring(0, licenseNo.length() - 1);
		}else{
			if(licenseNo.length() >= lenght) return;
			
			licenseNo += ((Button)view).getText();			
		}
		
		show.setText(licenseNo);
	}

	@Override
	public boolean onLongClick(View v) {
		
		if(v.getId() == R.id.show) {
			if(licenseNo == null || licenseNo.trim().length() == 0) return false;
			
			show.setBackgroundResource(R.drawable.text_show_sent);
			show.setTextColor(Color.parseColor("#000000"));
			
			if(isCheckOut) {
				Intent returnIntent = new Intent();
                returnIntent.putExtra("result", licenseNo);
				setResult(RESULT_OK, returnIntent);
				finish();
				return true;
			}
			
			VehicleSaveCriteriaReq req = new VehicleSaveCriteriaReq();
			req.licenseNo = licenseNo;
			req.deviceId = ApplicationScope.getInstance().deviceId;
			req.gateName = gateName;
			
			service.send(1, req, VehicleSaveCriteriaResp.class, "/restAct/vehicle/saveVehicleParking", HttpMethod.POST, this);
			spinner.show();
		} else {
			licenseNo = "";
			show.setText(licenseNo);
		}
		return true;
	}

	@Override
	public void onComplete(int id, Object result, Object passedParam) {
		try {
			VehicleSaveCriteriaResp resp = (VehicleSaveCriteriaResp)result;
			
			if(resp.statusCode != 9999) {
				new ErrorHandler(this).handler(resp);
				return;
			}
			
			licenseNo = "";
			show.setText(licenseNo);
			show.setBackgroundResource(R.drawable.text_show);
			show.setTextColor(Color.parseColor("#FFFFFF"));
			Toast.makeText(this, "Sent already", Toast.LENGTH_SHORT).show();			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			spinner.dismiss();
		}
	}

}
