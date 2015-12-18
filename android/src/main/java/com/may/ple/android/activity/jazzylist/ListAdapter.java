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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.may.ple.android.activity.R;
import com.may.ple.android.activity.criteria.Menu;

public class ListAdapter extends ArrayAdapter<Menu> {

    private final LayoutInflater inflater;
    private final Resources res;
    private final int itemLayoutRes;
    private List<Menu> menus;
    
    public ListAdapter(Context context, int itemLayoutRes, List<Menu> menus) {
        super(context, itemLayoutRes, R.id.text, menus);
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        this.itemLayoutRes = itemLayoutRes;
        this.menus = menus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(itemLayoutRes, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Menu menu = menus.get(position);
        
        if(menu.image.imageContentBase64 != null) {
        	byte[] decodedString = Base64.decode(menu.image.imageContentBase64, Base64.DEFAULT);        	
        	Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        	holder.img.setImageBitmap(decodedByte);
        }else{
        	Bitmap noImg = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_image_available);
        	holder.img.setImageBitmap(noImg);
        }
        
        if(menu.isRecommented) {
        	holder.imgRecomm.setVisibility(View.VISIBLE);
        }else{
        	holder.imgRecomm.setVisibility(View.GONE);        	
        }
        
        holder.view.setBackgroundColor(res.getColor(Utils.getBackgroundColorRes(position, itemLayoutRes)));
        holder.text.setText(menu.name + " " + String.format("%.2f", menu.price) + "-");

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
