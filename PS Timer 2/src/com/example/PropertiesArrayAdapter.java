package com.example;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

public class PropertiesArrayAdapter extends ArrayAdapter<TimerProperty> {

	private NewTimer context;
	private List<TimerProperty> timerProperties;

	public PropertiesArrayAdapter(NewTimer context,
			ArrayList<TimerProperty> timerProperties) {

		super(context, R.layout.rowbuttonlayout, timerProperties);
		this.context = context;
		this.setTimerProperties(timerProperties);
	}

	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
		public TextView crono;
		public TextView time;
		public Spinner spinner;
		public TextView type;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View view = convertView;

		TimerProperty actualTimerProperty = getItem(position);
		int timerId = actualTimerProperty.getId();
		String propertyName = actualTimerProperty.getPropertyName();
		Model currentModel = null;

		//not a new timer
		if (timerId != -1) {

			//get the timer the user is modifying
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			currentModel = databaseHelper.selectModelById(db, timerId);
			db.close();
		}

		//If the property is the name of the timer
		if (propertyName == TimerProperty.NAME) {
			
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.namepropertylayout, null);

			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.TimerName);

			if (timerId != -1) {
				viewHolder.text.setText(currentModel.getName());
			}

			return view;
		}

		//If the property is the time of the timer
		else if (propertyName == TimerProperty.TIME) {
			int hoursInt = 0;
			int minutesInt = 0;
			int secondsInt = 0;

			if (timerId != -1) {
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
				String formatted = format.format(currentModel.getTime()); 
				Date registeredTime = null;

				try {
					registeredTime = format.parse(formatted);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				hoursInt = registeredTime.getHours();
				minutesInt = registeredTime.getMinutes();
				secondsInt = registeredTime.getSeconds();
			}

			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.timepropertylayout, null);

			Object hours = view.findViewById(R.id.hours);
			Class<? extends Object> hoursClass = hours.getClass();

			Object minutes = view.findViewById(R.id.minutes);
			Class<? extends Object> minutesClass = minutes.getClass();

			Object seconds = view.findViewById(R.id.seconds);
			Class<? extends Object> secondsClass = seconds.getClass();

			try {
				
				Method hourMethod = hoursClass.getMethod("setRange", int.class,	int.class);
				hourMethod.invoke(hours, 00, 23);

				Method minutesMethod = minutesClass.getMethod("setRange", int.class, int.class);
				minutesMethod.invoke(minutes, 00, 59);

				Method secondsMethod = secondsClass.getMethod("setRange", int.class, int.class);
				secondsMethod.invoke(seconds, 00, 59);

				if (timerId != -1) {

					Method mhours = hoursClass.getMethod("setCurrent", int.class);
					mhours.invoke(hours, hoursInt);

					Method mminutes = minutesClass.getMethod("setCurrent", int.class);
					mminutes.invoke(minutes, minutesInt);

					Method mseconds = secondsClass.getMethod("setCurrent", int.class);
					mseconds.invoke(seconds, secondsInt);
				}

			} catch (Exception e) {
				Log.e("ERROR", e.getMessage());
			}

			return view;
		}

		//If the property is the proximity sensor
		else if (propertyName == TimerProperty.PROXIMITY) {

			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.proximitypropertylayout, null);

			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.type = (TextView) view.findViewById(R.id.Proximity);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.ProximityCheck);
			
			//not a new timer
			if (timerId != -1) {
				if (currentModel.isProximity() == 0) {
					viewHolder.type.setText("Not Active");
					viewHolder.checkbox.setChecked(false);
				}

				else {
					viewHolder.type.setText("Active");
					viewHolder.checkbox.setChecked(true);
				}
			}
			
			//new Timer
			else {
				viewHolder.type.setText("Not Active");
			}

			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {

					SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
					Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

					
					if (sensor == null) {
					//the device doesn't support the proximity sensor
						viewHolder.checkbox.setChecked(false);
						AlertDialog.Builder builder = new AlertDialog.Builder(context);

						builder.setMessage(
							"Proximity Sensor cannot be activated. No Proximity Sensor was detected on your device.")
							.setCancelable(false)
							.setNegativeButton("Close",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int id) {
											dialog.cancel();
										}
									})
							.setIcon(R.drawable.alert_dialog_icon)
							.setTitle("Information");
						
						AlertDialog alert = builder.create();
						alert.show();
					}

					else {

						if (viewHolder.checkbox.isChecked())
							viewHolder.type.setText("Active");
						else
							viewHolder.type.setText("Not Active");
						
					}
				}
			});

			return view;

		}

		//If the property is the type of the alarm
		else if (propertyName == TimerProperty.TYPE) {

			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.vibrationpropertylayout, null);

			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.type = (TextView) view.findViewById(R.id.TypeSelected);
			viewHolder.type.setTag(TimerProperty.TYPE);

			if (timerId != -1) {
				viewHolder.type.setText(currentModel.getType());
			}

			else {
				viewHolder.type.setText("Sound & Vibration");
			}

			return view;
		}

		return view;

	}

	public List<TimerProperty> getTimerProperties() {
		return timerProperties;
	}

	public void setTimerProperties(List<TimerProperty> timerProperties) {
		this.timerProperties = timerProperties;
	}

}
