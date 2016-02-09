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

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.may.ple.android.activity.ApplicationScope;
import com.may.ple.android.activity.R;
import com.may.ple.android.activity.criteria.CommonCriteriaResp;
import com.may.ple.android.activity.criteria.Menu;
import com.may.ple.android.activity.criteria.OrderSaveCriteriaReq;
import com.may.ple.android.activity.criteria.SubMenu;
import com.may.ple.android.activity.dialog.ProgressDialogSpinner;
import com.may.ple.android.activity.service.CenterService;
import com.may.ple.android.activity.service.RestfulCallback;
import com.may.ple.android.activity.utils.handler.ErrorHandler;

public class ListAdapter extends ArrayAdapter<Menu> {

    private final LayoutInflater inflater;
    private final Resources res;
    private final int itemLayoutRes;
    private List<Menu> menus;
    private final ProgressDialogSpinner spinner;
    private final ApplicationScope appScope;
    private List<SubMenu> subMenus;
    
    public ListAdapter(Context context, int itemLayoutRes, List<Menu> menus) {
        super(context, itemLayoutRes, R.id.text, menus);
        appScope = ApplicationScope.getInstance();
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        this.itemLayoutRes = itemLayoutRes;
        this.menus = menus;   
        spinner = new ProgressDialogSpinner(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Menu menu = menus.get(position);
        
        if (convertView == null) {
            convertView = inflater.inflate(itemLayoutRes, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            
            holder.view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final Menu m = menus.get(v.getId());
					subMenus = new ArrayList<>();
					
					final View view = inflater.inflate(R.layout.alert_dialog_order, null);
					ImageView img = (ImageView)view.findViewById(R.id.image);
					TextView name = (TextView)view.findViewById(R.id.name);
					name.setText(m.name + " " + String.format("%.2f", m.price) + "-");
					final TextView amount = (TextView)view.findViewById(R.id.amount);
					amount.setText("1");
					final CheckBox takeHome = (CheckBox)view.findViewById(R.id.take_home);
					final EditText comment = (EditText)view.findViewById(R.id.comment);
					
					final EditText ref = (EditText)view.findViewById(R.id.ref);
					final EditText tableName = (EditText)view.findViewById(R.id.tableName);		
					
					//------
					LinearLayout subMenuLayout1 = (LinearLayout)view.findViewById(R.id.subMenu);
					LinearLayout subMenuLayout2 = null;
					CheckBox checkBox;
					SubMenu subMenu;
					
					for (int i = 0; i < m.subMenus.size(); i++) {
						subMenuLayout2 = (LinearLayout)inflater.inflate(R.layout.sub_menu_layout, null);							
						subMenuLayout1.addView(subMenuLayout2);
						
						subMenu = m.subMenus.get(i);
						
						final LinearLayout subMenuLayout3 = (LinearLayout)inflater.inflate(R.layout.sub_menu, null);	
						checkBox = (CheckBox)subMenuLayout3.findViewById(R.id.text);
						checkBox.setId(i);
						
						checkBox.setText(String.format(subMenu.name + " %.2f", subMenu.price) + "-");
						subMenuLayout2.addView(subMenuLayout3);
						
						checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								EditText amount = (EditText)subMenuLayout3.findViewById(R.id.amount);
								SubMenu sm = m.subMenus.get(buttonView.getId());
								amount.setTag(sm.id);
								
								if(isChecked) {
									amount.setText("");
									amount.setVisibility(View.VISIBLE);
									subMenus.add(sm);
								} else {
									amount.setVisibility(View.GONE);
									subMenus.remove(sm);
								}
							}
						});
					}
					//--------
					
					tableName.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							tableName.setText(appScope.tableName);
							ref.setText(appScope.ref);
							return false;
						}
					});
					ref.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							tableName.setText(appScope.tableName);
							ref.setText(appScope.ref);
							return false;
						}
					});
					
					if(m.image.imageContentBase64 != null) {
						byte[] decodedString = Base64.decode(m.image.imageContentBase64, Base64.DEFAULT);        	
						Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
						img.setImageBitmap(decodedByte);						
					} else {
						img.setVisibility(View.GONE);
//						Bitmap noImg = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_image_available);
//			        	img.setImageBitmap(noImg);
					}
					
					SeekBar seekAmount = (SeekBar) view.findViewById(R.id.seekAmount);
					seekAmount.setProgress(0);
					seekAmount.setMax(9);
					seekAmount.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
							amount.setText("" + (progress + 1));
						}

						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
						}

						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub							
						}
					});
					
			        
					final AlertDialog dialog = new AlertDialog.Builder(getContext())
			        .setCancelable(false)
			        .setView(view)
			        .setPositiveButton(getContext().getResources().getText(R.string.order), new OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			            	
			            }
			        })
			        .setNegativeButton(getContext().getResources().getText(R.string.close), new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					}).show();
					
					Button positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
					positiveBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if(tableName.getText().toString() == null || tableName.getText().toString().trim().equals("")) {
								Toast.makeText(getContext(), "กรุณากรอกชื่อโต๊ะ", Toast.LENGTH_SHORT).show();		
								return;
							} else if(ref.getText().toString() == null || ref.getText().toString().trim().equals("")) {
								Toast.makeText(getContext(), "กรุณากรอกรหัสอ้างอิง", Toast.LENGTH_SHORT).show();																
								return;
							}
							
							//-----------------------------------------------------------
							LinearLayout subMenuLayout1 = (LinearLayout)view.findViewById(R.id.subMenu);
							SubMenu sm;
							String subAmount;
							
							for (int i = 0; i < subMenus.size(); i++) {
								sm = subMenus.get(i);
								subAmount = ((EditText)subMenuLayout1.findViewWithTag(sm.id)).getText().toString();
								
								if(subAmount.equals("")) {
									sm.amount = 1;
								} else {
									sm.amount = Integer.valueOf(subAmount);									
								}
							}
							
							OrderSaveCriteriaReq req = new OrderSaveCriteriaReq();
							req.menuId = m.id;
							req.tableName = tableName.getText().toString().trim();
							req.ref = ref.getText().toString().trim();
							req.amount = Integer.parseInt(amount.getText().toString());
							req.comment = comment.getText().toString().trim();
							req.isTakeHome = takeHome.isChecked();
							req.subMenus = subMenus;
														
							appScope.tableName = req.tableName;
							appScope.ref = req.ref;
							
							spinner.show();
							
							new CenterService(getContext()).send(0, req, CommonCriteriaResp.class, "/restAct/order/saveOrder", HttpMethod.POST, new RestfulCallback() {
								@Override
								public void onComplete(int id, Object result, Object passedParam) {	
									try {
										CommonCriteriaResp resp = (CommonCriteriaResp)result;
										
										if(resp.statusCode != 9999) {
											spinner.dismiss();
											new ErrorHandler(getContext()).handler(resp);
											return;
										}
										
										Toast.makeText(getContext(), getContext().getResources().getText(R.string.order_success), Toast.LENGTH_SHORT).show();
									} catch (Exception e) {
										
									} finally {
										spinner.dismiss();
										dialog.dismiss();
									}
								}
							});
						}
					});					
				}
			});
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        
        
        if(menu.image.imageContentBase64 != null) {
        	byte[] decodedString = Base64.decode(menu.image.imageContentBase64, Base64.DEFAULT);        	
        	Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        	holder.img.setImageBitmap(decodedByte);
        }else{
        	holder.img.setVisibility(View.GONE);
//        	Bitmap noImg = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_image_available);
//        	holder.img.setImageBitmap(noImg);
        }
        
        if(menu.isRecommented) {
        	holder.imgRecomm.setVisibility(View.VISIBLE);
        }else{
        	holder.imgRecomm.setVisibility(View.GONE);        	
        }
        
        holder.view.setId(position);
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
