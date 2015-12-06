package com.may.ple.android.activity.setting;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.may.ple.android.activity.R;

public class PreferenceActivitySetting extends PreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragmentSetting()).commit();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
