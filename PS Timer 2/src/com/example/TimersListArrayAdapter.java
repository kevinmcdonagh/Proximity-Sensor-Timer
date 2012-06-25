package com.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimersListArrayAdapter extends ArrayAdapter<Model> {
	
//	private static String TAG = "TimersListArrayAdapter";
	private final List<Model> list;
	private final Activity context;
	

	public TimersListArrayAdapter(Activity context, List<Model> list) {
		super(context, R.layout.rowbuttonlayout, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View view = null;
		final ViewHolder viewHolder;
				
		if (convertView == null) {
					
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.rowbuttonlayout, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.text = (TextView) view.findViewById(R.id.label);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.cronohours = (TextView) view.findViewById(R.id.cronohours);
			viewHolder.cronominutes = (TextView) view.findViewById(R.id.cronominutes);
			viewHolder.cronoseconds = (TextView) view.findViewById(R.id.cronoseconds);
			viewHolder.linear = (LinearLayout) view.findViewById(R.id.linear);		
			view.setTag(viewHolder);
		
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();			
		}
			
		final Model selectedTimer = (Model) getItem(position);
		selectedTimer.setModelView(view);		
		
		//Showing Timer name
		viewHolder.text.setText(list.get(position).getName());			
			
		//Showing Timer time
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String formatted = format.format(list.get(position).getTime()); // date is a long in milliseconds

		Date actualTimeCount = null;
		try {
			actualTimeCount = format.parse(formatted);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		int hours = actualTimeCount.getHours();
		int minutes = actualTimeCount.getMinutes();
		int seconds = actualTimeCount.getSeconds();
		
		if (hours<10) viewHolder.cronohours.setText("0"+hours);
		else viewHolder.cronohours.setText(""+hours);
		
		if (minutes<10) viewHolder.cronominutes.setText("0"+minutes);
		else viewHolder.cronominutes.setText(""+minutes);
		
		if (seconds<10) viewHolder.cronoseconds.setText("0"+seconds);
		else viewHolder.cronoseconds.setText(""+seconds);
		
		
		//If the checkbox is pressed
		viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
									
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {			
				
				//Start Service TimerService
				Intent intent= new Intent();	    	            
	    		intent.setComponent(new ComponentName(context,TimerService.class));	
	    		intent.putExtra("timerId", selectedTimer.getId());
	    		intent.putExtra("timerPosition", position);
	    		context.startService(intent);   	    		
			}
		});
	
		return view;
	}

}
