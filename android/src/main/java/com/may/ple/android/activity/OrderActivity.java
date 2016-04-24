package com.may.ple.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.may.ple.android.activity.criteria.Menu;
import com.may.ple.android.activity.criteria.SubMenu;

public class OrderActivity extends SherlockActivity {
	public static Menu m;
	private ApplicationScope appScope;
	private List<SubMenu> subMenus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog_order);
		appScope = ApplicationScope.getInstance();
		
		subMenus = new ArrayList<>();
		ImageView img = (ImageView)findViewById(R.id.image);
		TextView name = (TextView)findViewById(R.id.name);
		name.setText(m.name + " " + String.format("%.2f", m.price) + "-");
		final TextView amount = (TextView)findViewById(R.id.amount);
		amount.setText("1");
		final CheckBox takeHome = (CheckBox)findViewById(R.id.take_home);
//		final EditText comment = (EditText)findViewById(R.id.comment);
		
		final EditText ref = (EditText)findViewById(R.id.ref);
		final EditText tableName = (EditText)findViewById(R.id.tableName);	
		
		
		/*LinearLayout subMenuLayout1 = (LinearLayout)findViewById(R.id.subMenu);
		LinearLayout subMenuLayout2 = null;
		CheckBox checkBox;
		SubMenu subMenu;
		LayoutInflater inflater = LayoutInflater.from(this);
		
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
		}*/
		
		
		
		
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
//			Bitmap noImg = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_image_available);
//        	img.setImageBitmap(noImg);
		}
		
		SeekBar seekAmount = (SeekBar)findViewById(R.id.seekAmount);
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
		
		
		
	}

}
