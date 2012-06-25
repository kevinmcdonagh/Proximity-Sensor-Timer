package com.example;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AlarmSound extends Activity {

	MediaPlayer mMediaPlayer = null;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Not show window title
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.alarmsound);

		// Flags to show the screen even if the screen is locked
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Getting extras
		Bundle bundle = this.getIntent().getExtras();
		int timerId = bundle.getInt("timerId");

		// Getting the timer from the database
		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		final Model model = databaseHelper.selectModelById(db, timerId);
		db.close();

		// Showing the name
		TextView title = (TextView) findViewById(R.id.text);
		title.setText(model.getName());

		//Getting Vibrator
		final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		final long[] pattern = { 0, 200, 500 };

		Button stopButton = (Button) findViewById(R.id.stop);

		//When pressing stop Button stop mediaPlayer and/or vibration
		stopButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				// Stop the Alarm
				if ((model.getType().matches("Sound & Vibration"))|| (model.getType().matches("Sound")))
					mMediaPlayer.stop();
				if ((model.getType().matches("Sound & Vibration"))|| (model.getType().matches("Vibration")))
					vibrator.cancel();
				finish();
			}
		});

		// Setting alarm ringtone
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			// alert is null, using backup
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				// I can't see this ever being null (as always
				// have a default notification) but just in case
				
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}

		//creating MediaPlayer
		mMediaPlayer = new MediaPlayer();

		try {
			mMediaPlayer.setDataSource(this, alert);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		//Start mediaPlayer
		if ((model.getType().matches("Sound & Vibration"))
				|| (model.getType().matches("Sound"))) {
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.setLooping(true);
				try {
					mMediaPlayer.prepare();
					mMediaPlayer.start();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		//Start vibration
		if ((model.getType().matches("Sound & Vibration"))
				|| (model.getType().matches("Vibration")))
			vibrator.vibrate(pattern, 0);

		// Proximity Sensor
		if (model.isProximity() == 1) {
			
			final SensorManager sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
			Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

			Calendar cal = Calendar.getInstance();
			cal.getTimeInMillis();

			Time now = new Time();
			now.setToNow();

			final long firstTimeInMillis = Calendar.getInstance().getTimeInMillis();

			SensorEventListener listener = new SensorEventListener() {

				public void onSensorChanged(SensorEvent e) {

					if (e.sensor.getType() == Sensor.TYPE_PROXIMITY) {

						Calendar cal2 = Calendar.getInstance();

						if (cal2.getTimeInMillis() > firstTimeInMillis + 600) {
							// if the proximity sensor detects that there is
							// something close after 600 millis 
							//I had to create this condition to make it work in an htc device
							if (e.values[0] == 0) {
								if ((model.getType()
										.matches("Sound & Vibration"))
										|| (model.getType().matches("Sound")))
									mMediaPlayer.stop();
								if ((model.getType()
										.matches("Sound & Vibration"))
										|| (model.getType()
												.matches("Vibration")))
									vibrator.cancel();
								
								finish();
								sm.unregisterListener(this);

							}
						}

					}
				}

				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub

				}
			};

			//Register Proximity Sensor
			sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}

		//After 20 seconds we stop the alarm automatically
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					sleep(20 * 1000);
					if (mMediaPlayer.isPlaying()) {
						if ((model.getType().matches("Sound & Vibration"))
								|| (model.getType().matches("Sound")))
							mMediaPlayer.stop();
						if ((model.getType().matches("Sound & Vibration"))
								|| (model.getType().matches("Vibration")))
							vibrator.cancel();
						finish();
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		};

		thread.start();

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
