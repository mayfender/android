package com.may.ple.android.activity;

import org.springframework.http.HttpMethod;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.may.ple.android.activity.criteria.GetMenuCriteriaResp;
import com.may.ple.android.activity.dialog.ProgressDialogSpinner;
import com.may.ple.android.activity.jazzylist.ListAdapter;
import com.may.ple.android.activity.service.CenterService;
import com.may.ple.android.activity.service.RestfulCallback;
import com.may.ple.android.activity.utils.handler.ErrorHandler;
import com.twotoasters.jazzylistview.JazzyGridView;

public class ViewMenuAction extends SherlockActivity implements RestfulCallback {
	private ProgressDialogSpinner spinner;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_menu);
        
        long id = getIntent().getLongExtra("id", 0);
        String parentMenuType = getIntent().getStringExtra("parentMenuType");
        
        setTitle(parentMenuType);
        
        spinner = new ProgressDialogSpinner(this);
        spinner.show();
        CenterService service = new CenterService(this);
        service.send(1, null, GetMenuCriteriaResp.class, "/restAct/loadData/getMenus?id=" + id, HttpMethod.GET, this);
    }

	@Override
	public void onComplete(int id, Object result, Object passedParam) {
		try {
			if(id == 1) {
				GetMenuCriteriaResp resp = (GetMenuCriteriaResp)result;	
				
				if(resp.statusCode != 9999) {
					new ErrorHandler(this).handler(resp);
					return;
				} 
				
				JazzyGridView mGrid = (JazzyGridView) findViewById(android.R.id.list);
		        mGrid.setAdapter(new ListAdapter(this, R.layout.grid_item, resp.menus));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			spinner.dismiss();
		}
	}
	
}
