package com.may.ple.android.activity;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.may.ple.android.activity.criteria.Menu;
import com.may.ple.android.activity.jazzylist.ListAdapter;
import com.twotoasters.jazzylistview.JazzyGridView;

public class ViewMenuFragment extends SherlockFragment {
	public List<Menu> menus;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_menu, container, false);
        
        JazzyGridView mGrid = (JazzyGridView) v.findViewById(android.R.id.list);
		mGrid.setAdapter(new ListAdapter(this.getActivity(), R.layout.grid_item, menus));
        
        return v;
    }
    
}
