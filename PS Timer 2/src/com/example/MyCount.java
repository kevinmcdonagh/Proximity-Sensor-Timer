package com.example;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.CountDownTimer;

public class MyCount extends CountDownTimer implements SensorEventListener {

	private boolean started;

	public MyCount(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		setStarted(false);
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public void stop() {

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTick(long arg0) {
		// TODO Auto-generated method stub
	}

}
