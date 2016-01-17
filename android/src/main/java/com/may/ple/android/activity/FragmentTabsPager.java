/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.may.ple.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpMethod;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.Menu;
import com.may.ple.android.activity.criteria.GetMenuTypeCriteriaResp;
import com.may.ple.android.activity.criteria.MenuType;
import com.may.ple.android.activity.dialog.ProgressDialogSpinner;
import com.may.ple.android.activity.service.CenterService;
import com.may.ple.android.activity.service.RestfulCallback;
import com.may.ple.android.activity.setting.PreferenceActivitySetting;
import com.may.ple.android.activity.utils.handler.ErrorHandler;

/**
 * Demonstrates combining a TabHost with a ViewPager to implement a tab UI
 * that switches between tabs and also allows the user to perform horizontal
 * flicks to move between the tabs.
 */
public class FragmentTabsPager extends SherlockFragmentActivity implements RestfulCallback {
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    private ProgressDialogSpinner spinner;
	private Button tryConn = null;
	private boolean isLoginError;
	private Bundle savedInstanceState;
	private CenterService service;
	private ApplicationScope appScope;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        appScope = ApplicationScope.getInstance();
        
        setContentView(R.layout.fragment_tabs_pager);
        
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        spinner = new ProgressDialogSpinner(this);
        spinner.show();
        
        service = new CenterService(this);
        service.send(2, null, GetMenuTypeCriteriaResp.class, "/restAct/loadData/getMenuType", HttpMethod.GET, FragmentTabsPager.this);
    }

    @Override
	public void onComplete(int id, Object result, Object passedParam) {
		try {
			if(id == 2) {
				GetMenuTypeCriteriaResp resp = (GetMenuTypeCriteriaResp)result;
				
				if(resp.statusCode != 9999) {					
					optionErrorLogin(true);
					
					tryConn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							optionErrorLogin(false);
							service.reloadSetting();
							
							spinner.show();
							service.send(2, null, GetMenuTypeCriteriaResp.class, "/restAct/loadData/load", HttpMethod.GET, FragmentTabsPager.this);											
						}
					});
					
					new ErrorHandler(this).handler(resp);
				} else {
					if (savedInstanceState != null) {
						mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
					}
					
					TelephonyManager telMrg = (TelephonyManager)FragmentTabsPager.this.getSystemService(Context.TELEPHONY_SERVICE);
					ApplicationScope.getInstance().deviceId = telMrg.getDeviceId();
					
					mViewPager = (ViewPager)findViewById(R.id.pager);
					mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
					
					Set<String> keySet = resp.menuTypesMap.keySet();
					
					for (String key : keySet) {
						List<MenuType> menuTypes = resp.menuTypesMap.get(key);
						List<com.may.ple.android.activity.criteria.Menu> menus = resp.menusMap.get(key);
						mTabsAdapter.addTab(mTabHost.newTabSpec(key).setIndicator(key), ViewMenuTypeFragment.class, null, menuTypes, menus, key);
					}
				}
			}
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
		} finally {
			spinner.dismiss();
		}
	}
    
    
    private void optionErrorLogin(boolean isShow) {
    	
    	// Show button try to connect when login error.
    	tryConn = (Button)findViewById(R.id.tryConn);
		tryConn.setVisibility(isShow ? View.VISIBLE : View.GONE);
		
		// Show option menu setting when login error.
		isLoginError = isShow;
		invalidateOptionsMenu();
    }
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        if(isLoginError) {
        	getSupportMenuInflater().inflate(R.menu.settings_action_provider, menu);
        }
        
        return true;
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
    

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter
            implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TabHost mTabHost;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private final List<MenuType> menuTypes;
            private final List<com.may.ple.android.activity.criteria.Menu> menus;
            private final String parentMenuType;
            
            TabInfo(String _tag, Class<?> _class, Bundle _args, List<MenuType> _menuTypes, List<com.may.ple.android.activity.criteria.Menu> _menus, String _parentMenuType) {
                tag = _tag;
                clss = _class;
                args = _args;
                menuTypes = _menuTypes;
                menus = _menus;
                parentMenuType = _parentMenuType;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args, List<MenuType> menuTypes, List<com.may.ple.android.activity.criteria.Menu> menus, String parentMenuType) {
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args, menuTypes, menus, parentMenuType);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            ViewMenuTypeFragment instantiate = (ViewMenuTypeFragment)Fragment.instantiate(mContext, info.clss.getName(), info.args);
            instantiate.menuTypes = info.menuTypes;
            instantiate.menus = info.menus;
            instantiate.parentMenuType = info.parentMenuType;
            return instantiate;  
        }

        @Override
        public void onTabChanged(String tabId) {
            int position = mTabHost.getCurrentTab();
            mViewPager.setCurrentItem(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not in touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
    
    //------------------------------: Setting Action Bar :---------------------------------------
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
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  	    if(keyCode == KeyEvent.KEYCODE_BACK) {
  	        new AlertDialog.Builder(this)
  	        .setIcon(android.R.drawable.ic_dialog_alert)
  	        .setTitle(R.string.exit_app_title)
  	        .setMessage(R.string.exit_app_msg)
  	        .setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
  	            @Override
  	            public void onClick(DialogInterface dialog, int which) {
  	            	appScope.tableName = null;
  	            	appScope.ref = null;
  	                finish();    
  	            }
  	        })
  	        .setNegativeButton(R.string.cancel_btn, null)
  	        .show();

  	        return true;
  	    } else {
  	        return super.onKeyDown(keyCode, event);
  	    }
  	}
    
}
