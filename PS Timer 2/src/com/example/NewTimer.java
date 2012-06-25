package com.example;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class NewTimer extends Activity {

	private int timerId;
	private String timerNam = "";
	private String timerTyp = "";
	private int proxim = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.newtimer);

		Bundle bundle = this.getIntent().getExtras();

		// If we are modifing a Timer
		if (bundle != null) {
			timerId = bundle.getInt("timerId");

			// getting Timer from the database
			DatabaseHelper databaseHelper = new DatabaseHelper(this);
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			Model currentModel = databaseHelper.selectModelById(db, timerId);
			db.close();

			TextView title = (TextView) findViewById(R.id.labelGelete);
			title.setText("Edit Timer");
			timerNam = currentModel.getName();
			timerTyp = currentModel.getType();
			setProxim(currentModel.isProximity());
		}

		// If it's a new Timer
		else {
			timerId = -1;
			TextView title = (TextView) findViewById(R.id.labelGelete);
			title.setText("New Timer");
			timerNam = "New Timer";
			timerTyp = "Sound & Vibration";
		}

		final ListView listView = (ListView) findViewById(R.id.list);
		listView.requestFocus();

		ArrayAdapter<TimerProperty> adapter = new PropertiesArrayAdapter(this, getTimerProperties());
		listView.setAdapter(adapter);

		// If the user click on an item of the list
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				TimerProperty selectedProperty = (TimerProperty) listView.getAdapter().getItem(position);

				// If the user selects the property type of Alarm
				if (selectedProperty.getPropertyName().equals("type")) {
					
					final CharSequence[] items = { "Sound", "Vibration", "Sound & Vibration" };

					//create alert dialog
					AlertDialog.Builder builder = new AlertDialog.Builder(NewTimer.this);
					builder.setTitle("Select the Type of Alarm");
					builder.setSingleChoiceItems(items, -1,	new DialogInterface.OnClickListener() {

						// onClick
						public void onClick(DialogInterface dialog,	int item) {

							dialog.dismiss();

							// We change the selection the user has made
							// in the Timer Detail Screen
							TextView type = (TextView) listView.findViewById(R.id.TypeSelected);
							type.setText((String) items[item]);
							timerTyp = (String) items[item];
						}
					});
					
					AlertDialog alert = builder.create();
					alert.show();

				}

				// If the user selects the property name of the Alarm
				else if (selectedProperty.getPropertyName().equals("name")) {

					AlertDialog.Builder builder = new AlertDialog.Builder(NewTimer.this);
					builder.setTitle("Timer Name");

					// Set an EditText view to get user input
					final EditText input = new EditText(NewTimer.this);
					input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
					input.setText(timerNam);
					builder.setView(input);

					builder.setPositiveButton("Ok",	new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
							TextView name = (TextView) listView.findViewById(R.id.TimerName);
							name.setText(input.getText());
							timerNam = name.getText().toString();	
						}
					});

					builder.setNegativeButton("Cancel",	new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int whichButton) {
							// Canceled.
						}
					});

					AlertDialog alert = builder.create();
					alert.show();

				}

				// If the user selects Property Activate/Deactivate proximity Sensor
				else if (selectedProperty.getPropertyName().equals("proximity")) {
					
					TextView proxi = (TextView) listView.findViewById(R.id.Proximity);
					CheckBox check = (CheckBox) listView.findViewById(R.id.ProximityCheck);

					if (!check.isChecked()) {
						
						SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
						Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

						if (sensor == null) {

							AlertDialog.Builder builder = new AlertDialog.Builder(NewTimer.this);

							builder.setMessage(
									"Proximity Sensor cannot be activated. No Proximity Sensor was detected on your device.")
									.setCancelable(false)

									.setNegativeButton(	"Close",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,	int id) {
												dialog.cancel();
											}
										})
									.setIcon(R.drawable.alert_dialog_icon)
									.setTitle("Information");
							
							AlertDialog alert = builder.create();
							alert.show();
						}

						else {
							proxim = 1;
							proxi.setText("Active");
							check.setChecked(true);
						}

					}

					else {
						proxim = 0;
						proxi.setText("Not Active");
						check.setChecked(false);
					}

				}

			}
		});

		// Cancel Button
		Button cancelButton = (Button) findViewById(R.id.cancel);

		cancelButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {

				finish();
			}
		});

		// Save Button
		Button saveButton = (Button) findViewById(R.id.save);

		final DatabaseHelper databaseHelper = new DatabaseHelper(this);
		final SQLiteDatabase db = databaseHelper.getWritableDatabase();
		final boolean anyTimerStarted = databaseHelper.isAnyTimerActive(db);
		db.close();

		saveButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {

				listView.requestFocus();

				if (anyTimerStarted) {
				// Create Dialog showing that there are Timers Running and they will be reset
					
					AlertDialog.Builder builder = new AlertDialog.Builder(NewTimer.this);

					builder.setMessage(
						"All the timers will be reinitialized after this action. Do you want to proceed?")
						.setCancelable(false)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

							// If the user wants to continue
							public void onClick(DialogInterface dialog, int id) {

								try {
									final CheckBox proximitycheck = (CheckBox) listView
											.findViewById(R.id.ProximityCheck);

									int hours = 0;
									int minutes = 0;
									int seconds = 0;

									Object ohours = listView.findViewById(R.id.hours);
									Class<? extends Object> chours = ohours.getClass();

									Object ominutes = listView.findViewById(R.id.minutes);
									Class<? extends Object> cminutes = ominutes.getClass();

									Object oseconds = listView.findViewById(R.id.seconds);
									Class<? extends Object> cseconds = oseconds.getClass();

									try {
										Method mhours = chours.getMethod("getCurrent");
										hours = (Integer) mhours.invoke(ohours);

										Method mminutes = cminutes.getMethod("getCurrent");
										minutes = (Integer) mminutes.invoke(ominutes);

										Method mseconds = cseconds.getMethod("getCurrent");
										seconds = (Integer) mseconds.invoke(oseconds);

									} catch (Exception e) {
										Log.e("ERROR",	e.getMessage());
									}

									String date = hours + ":"+ minutes + ":"+ seconds;

									ContentValues cv = new ContentValues();
									cv.put(DatabaseHelper.TIMER_NAME, timerNam);
									cv.put("time", date);
									cv.put("proximity",	proximitycheck.isChecked());
									cv.put("duration", 1000 * 7);
									cv.put("type", timerTyp);

									final SQLiteDatabase db = databaseHelper.getWritableDatabase();

									//If the user is modifying an existing timer
									if (timerId != -1) db.update("timers", cv, "id=" + timerId,	null);

									//If the user is creating a new timer
									else {	db.insert("timers", "id", cv); }

									db.close();

									Intent intent = new Intent();
									intent.setComponent(new ComponentName(getBaseContext(),
											TimerServiceActivity.class));
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									overridePendingTransition(R.anim.push_up_out, R.anim.push_left_in);

									stopService(new Intent(NewTimer.this, TimerService.class));

								} catch (Exception ex) {
									Log.i("New Timer",
											ex.getMessage());

								}
							}
						})

						// Cancel Button
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								}).setIcon(R.drawable.alert_dialog_icon)
						.setTitle("Warning");

					AlertDialog alert = builder.create();
					alert.show();

				}

				// if any timer has started
				else {
					
					TextView timerName = (TextView) listView.findViewById(R.id.TimerName);
					CheckBox proximitycheck = (CheckBox) listView.findViewById(R.id.ProximityCheck);
					TextView timerType = (TextView) listView.findViewById(R.id.TypeSelected);

					if (timerName == null)
						timerName = (TextView) listView.findViewById(R.id.TimerName);

					if (timerType == null) timerType = (TextView) listView.findViewById(R.id.TypeSelected);

					try {
						int hours = 0;
						int minutes = 0;
						int seconds = 0;

						Object ohours = listView.findViewById(R.id.hours);
						Class<? extends Object> chours = ohours.getClass();

						Object ominutes = listView.findViewById(R.id.minutes);
						Class<? extends Object> cminutes = ominutes.getClass();

						Object oseconds = listView.findViewById(R.id.seconds);
						Class<? extends Object> cseconds = oseconds.getClass();

						try {
							Method mhours = chours.getMethod("getCurrent");
							hours = (Integer) mhours.invoke(ohours);

							Method mminutes = cminutes.getMethod("getCurrent");
							minutes = (Integer) mminutes.invoke(ominutes);

							Method mseconds = cseconds.getMethod("getCurrent");
							seconds = (Integer) mseconds.invoke(oseconds);

						}

						catch (Exception e) {
							Log.e("ERROR", e.getMessage());
						}

						String date = hours + ":" + minutes + ":" + seconds;

						ContentValues cv = new ContentValues();
						cv.put(DatabaseHelper.TIMER_NAME, timerNam);
						cv.put("time", date);
						cv.put("proximity", proximitycheck.isChecked());
						cv.put("duration", 1000 * 7);
						cv.put("type", timerTyp);

						final SQLiteDatabase db = databaseHelper
								.getWritableDatabase();

						if (timerId != -1)
							db.update("timers", cv, "id=" + timerId, null);

						else {
							db.insert("timers", "id", cv);
						}
						;

						db.close();

						Intent intent = new Intent();
						intent.setComponent(new ComponentName(getBaseContext(),
								TimerServiceActivity.class));
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						overridePendingTransition(R.anim.push_up_out,
								R.anim.push_left_in);

					} catch (Exception ex) {
						Log.i("New Timer", ex.getMessage());

					}
				}

			}
		});
	}

	public ArrayList<TimerProperty> getTimerProperties() {

		ArrayList<TimerProperty> list = new ArrayList<TimerProperty>();

		// Properties showed in the Timer Detail
		list.add(new TimerProperty(TimerProperty.NAME, timerId));
		list.add(new TimerProperty(TimerProperty.TIME, timerId));
		list.add(new TimerProperty(TimerProperty.TYPE, timerId));
		list.add(new TimerProperty(TimerProperty.PROXIMITY, timerId));

		return list;

	}

	public void setProxim(int proxim) {
		this.proxim = proxim;
	}

	public int getProxim() {
		return proxim;
	}

}
