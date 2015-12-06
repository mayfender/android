package com.may.ple.android.activity;

import java.util.Map;

import org.springframework.http.HttpMethod;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.may.ple.android.activity.criteria.VehicleCheckOutCriteriaReq;
import com.may.ple.android.activity.criteria.VehicleCheckOutCriteriaResp;
import com.may.ple.android.activity.dialog.ProgressDialogSpinner;
import com.may.ple.android.activity.service.CenterService;
import com.may.ple.android.activity.service.RestfulCallback;
import com.may.ple.android.activity.utils.constant.SettingKey;
import com.may.ple.android.activity.utils.handler.ErrorHandler;
import com.may.ple.parking.gateway.activity.R;
import com.may.ple.parking.gateway.activity.R.id;
import com.may.ple.parking.gateway.activity.R.layout;

public class GateOutActivity extends SherlockActivity implements RestfulCallback {
	private int reasonNoScan;
	private CenterService service;
	private ProgressDialogSpinner spinner;
	private String gateName;
	private AlertDialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gate_out);
		View type = findViewById(R.id.type);
		registerForContextMenu(type);
		service = new CenterService(this, this);
		spinner = new ProgressDialogSpinner(this);
		
		SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(this);
        gateName = setting.getString(SettingKey.gateName, null);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("เหตุผลที่เลือกใช้การพิมพ์");
		menu.add(0, 1, 0, "Ticket หาย");
		menu.add(0, 2, 1, "Ticket ไม่สามารถสแกนได้");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {	
		reasonNoScan = item.getItemId();
		Intent intent = new Intent(this, GateInActivity.class);
		intent.putExtra("isCheckOut", true);
		startActivityForResult(intent, 1);
		return true;
	}

	public void onClick(View v) {
		if(v.getId() == R.id.scan) {
			Intent intent = new Intent(this, BarcodeScanner.class);			
			startActivity(intent);
		} else if(v.getId() == R.id.type) {
			openContextMenu(v);
		} else if(v.getId() == R.id.lastCheckOut && dialog != null) {
			dialog.show();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String licenseNo = data.getStringExtra("result");                
                
                VehicleCheckOutCriteriaReq req = new VehicleCheckOutCriteriaReq();
                req.licenseNo = licenseNo;
                req.reasonNoScan = reasonNoScan;
                req.deviceId = ApplicationScope.getInstance().deviceId;
                req.gateName = gateName;
                
    			service.passedParam = req;
    			service.send(1, req, VehicleCheckOutCriteriaResp.class, "/restAct/vehicle/checkOutVehicle", HttpMethod.POST);
    			spinner.show();
            }
        } 
    }

	@Override
	public void onComplete(int id, Object obj, Object passedParam) {
		try {
			VehicleCheckOutCriteriaResp resp = (VehicleCheckOutCriteriaResp)obj;
			
			if(resp.statusCode != 9999) {
				new ErrorHandler(this).handler(resp);
				return;
			}
			
			VehicleCheckOutCriteriaReq req = (VehicleCheckOutCriteriaReq)passedParam;
			Map<String, Long> diffMap = resp.vehicleParking.dateTimeDiffMap;
			String dateFormat = "%1$td-%1$tm-%1$tY %1$tH:%1$tM:%1$tS";
			
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.alert_dialog_checkout, null);
			((TextView)view.findViewById(R.id.licenseNo)).setText(" : " + req.licenseNo);
			((TextView)view.findViewById(R.id.dateIn)).setText(" : " + String.format(dateFormat, resp.vehicleParking.inDateTime));
			((TextView)view.findViewById(R.id.dateOut)).setText(" : " + String.format(dateFormat, resp.vehicleParking.outDateTime));
			((TextView)view.findViewById(R.id.parkingTime)).setText(" : " + diffMap.get("hours") + ":" +diffMap.get("minutes") + " Hrs:Mins");
			((TextView)view.findViewById(R.id.price)).setText("( Price " + String.valueOf(resp.vehicleParking.price) + " Baht )");
			
			dialog = new AlertDialog.Builder(this)
	        .setCancelable(false)
	        .setView(view)
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	
	            }
	        })
	        .show();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			spinner.dismiss();
		}
	}

}
