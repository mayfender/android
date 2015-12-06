package com.may.ple.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;

public class GateSelectionActivity extends SherlockActivity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gate_selection);
		
		Button gatewayIn = (Button)findViewById(R.id.gatewayIn);
		Button gatewayOut = (Button)findViewById(R.id.gatewayOut);
	
		gatewayIn.setOnClickListener(this);
		gatewayOut.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.gatewayIn) {
			
		} else if(v.getId() == R.id.gatewayOut) {
			
		}
	}

}
