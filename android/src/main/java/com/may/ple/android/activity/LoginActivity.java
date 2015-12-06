package com.may.ple.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.Menu;
import com.may.ple.android.activity.criteria.LoginCriteriaResp;
import com.may.ple.android.activity.dialog.ProgressDialogSpinner;
import com.may.ple.android.activity.service.CenterService;
import com.may.ple.android.activity.service.RestfulCallback;
import com.may.ple.android.activity.setting.PreferenceActivitySetting;
import com.may.ple.android.activity.utils.handler.ErrorHandler;

public class LoginActivity extends SherlockActivity implements OnClickListener, RestfulCallback {
	private Integer selectedGate;
	private ProgressDialogSpinner spinner;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        findViewById(R.id.login_button).setOnClickListener(this);
        spinner = new ProgressDialogSpinner(this);
    }
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.settings_action_provider, menu);
        return true;
    }
    
    
    @Override
	public void onClick(View v) {
    	
    	if(selectedGate == null) {
    		Toast.makeText(this, getString(R.string.please_select_gate), Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	String username = ((EditText)findViewById(R.id.user_name)).getText().toString().trim();
    	String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
    	
    	if(username == null || username.isEmpty()) {
    		Toast.makeText(this, getString(R.string.please_fill_username), Toast.LENGTH_SHORT).show();    		
    		return;
    	}
    	if(password == null || password.isEmpty()) {
    		Toast.makeText(this, getString(R.string.please_fill_password), Toast.LENGTH_SHORT).show();
    		return;    		
    	}
    	
    	spinner.show();
    	new CenterService(this, this).login(username, password, "/user");
	}
    
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton)view).isChecked();
        
        switch(view.getId()) {
            case R.id.checkIn:
                if (checked) selectedGate = 1;
                break;
            case R.id.checkOut:
                if (checked) selectedGate = 2;
                break;
        }
    }
    
    
    public static class SettingsActionProvider extends ActionProvider {
        private final Context mContext;

        public SettingsActionProvider(Context context) {
            super(context);
            mContext = context;
        }
        
        @Override
        public View onCreateActionView() {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.settings_action_provider, null);
            ImageButton button = (ImageButton) view.findViewById(R.id.button);
            
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	Intent i = new Intent(mContext, PreferenceActivitySetting.class);
                	mContext.startActivity(i);
                }
            });
            return view;
        }
    }


	@Override
	public void onComplete(int id, Object result, Object passedParam) {
		try {
			LoginCriteriaResp resp = (LoginCriteriaResp)result;
			
			if(resp.statusCode != 9999 || !resp.authenticated) {
				new ErrorHandler(this).handler(resp);
				return;
			}
			
			TelephonyManager telMrg = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
			ApplicationScope.getInstance().deviceId = telMrg.getDeviceId();
			
	    	Intent intent = null;
	    	if(selectedGate == 1) {
	    		intent = new Intent(this, GateInActivity.class);
	    	} else if(selectedGate == 2) {
	    		intent = new Intent(this, GateOutActivity.class);    		
	    	}
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivity(intent);
	    	overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
		} finally {
			spinner.dismiss();
		}
	}


}

