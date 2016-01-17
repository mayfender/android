/*
 * Copyright (C) 2015 Two Toasters
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
package com.may.ple.android.activity.jazzylist;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.may.ple.android.activity.R;
import com.may.ple.android.activity.ViewMenuAction;
import com.may.ple.android.activity.criteria.MenuType;

public class ListMenuTypeAdapter extends ArrayAdapter<MenuType> {

    private final LayoutInflater inflater;
    private final Resources res;
    private final int itemLayoutRes;
    private List<MenuType> menuTypes;
    private String parentMenuType;
    
    public ListMenuTypeAdapter(Context context, int itemLayoutRes, List<MenuType> menuTypes, String parentMenuType) {
        super(context, itemLayoutRes, R.id.text, menuTypes);
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        this.itemLayoutRes = itemLayoutRes;
        this.menuTypes = menuTypes;   
        this.parentMenuType = parentMenuType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        MenuType menuType = menuTypes.get(position);
        
        if (convertView == null) {
            convertView = inflater.inflate(itemLayoutRes, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            
            holder.view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MenuType menuType = menuTypes.get(v.getId());
					
					Intent i = new Intent(getContext(), ViewMenuAction.class);
					i.putExtra("id", menuType.id);
					i.putExtra("parentMenuType", parentMenuType + " >> " + menuType.name);
                	getContext().startActivity(i);
					
				}
			});
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.view.setId(position);
        holder.view.setBackgroundColor(res.getColor(Utils.getBackgroundColorRes(position, itemLayoutRes)));
        holder.text.setText(menuType.name);

        return convertView;
    }

    static class ViewHolder {
        final TextView text;
        final ImageView img;
        final ImageView imgRecomm;
        final View view;

        ViewHolder(View view) {
        	this.view = view;
            text = (TextView) view.findViewById(R.id.text);
            img = (ImageView) view.findViewById(R.id.image);
            imgRecomm = (ImageView) view.findViewById(R.id.recomm);
        }
    }
    
}
