package com.may.ple.android.activity.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.may.ple.android.activity.utils.constant.SettingKey;
import com.may.ple.parking.gateway.activity.R;

public class PreferenceFragmentSetting extends PreferenceFragment implements OnPreferenceChangeListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_setting);
		EditTextPreference parkingCenterIp = (EditTextPreference)findPreference(SettingKey.parkingCenterIp);
		EditTextPreference parkingCenterPort = (EditTextPreference)findPreference(SettingKey.parkingCenterPort);
		EditTextPreference gateName = (EditTextPreference)findPreference(SettingKey.gateName);
		
		SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(getActivity());
		parkingCenterIp.setSummary(setting.getString(SettingKey.parkingCenterIp, null));
		parkingCenterPort.setSummary(setting.getString(SettingKey.parkingCenterPort, null));
		gateName.setSummary(setting.getString(SettingKey.gateName, null));
		
		parkingCenterIp.setOnPreferenceChangeListener(this);
		parkingCenterPort.setOnPreferenceChangeListener(this);
		gateName.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		preference.setSummary(newValue.toString());			
		return true;
	}

}
