package com.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class TimerService extends Service{
	
	private static String TAG = "TimerService";

	private static TimerServiceActivity mainActivity;
	private int numberOfTimers=0;
	private CountDownTimer[] countersList = new CountDownTimer[numberOfTimers];

	public static final String UPDATE_TIMER = "Update Timer";
	public static final String RESET_TIMER = "Reset Timer";
	public static final String FINISH_TIMER = "Finish Timer";
    Intent updateTimer;
    Intent resetTimer;
    Intent finishTimer;
    
    @Override
    public void onCreate() {
        super.onCreate();
        updateTimer = new Intent(UPDATE_TIMER);	
        resetTimer = new Intent(RESET_TIMER);	
        finishTimer = new Intent(FINISH_TIMER);
    }
    
	
	@Override
	public void onStart(Intent intent, int startid) {
	//code to execute when the service is starting up
				
		if (mainActivity==null) this.onDestroy();
		
		numberOfTimers = mainActivity.getAllTimers().size();
		
		//updating the list of countDown timers if necessary
		if (countersList.length!=numberOfTimers) countersList = new CountDownTimer[numberOfTimers];
		    	
    	//getting variables
		Bundle bundle = intent.getExtras();
		int timerId = bundle.getInt("timerId");
		final int timerPosition = bundle.getInt("timerPosition");
		
		Log.d(TAG,"Service called with timerId: "+timerId+" and timerPosition: "+timerPosition);
		
		//getting the timer from the DataBase
		DatabaseHelper databaseHelper = new DatabaseHelper(this);
    	SQLiteDatabase db = databaseHelper.getWritableDatabase();	
		
		final Model model = databaseHelper.selectModelById(db, timerId);
			
		//if the counter has not started
		if (model.getStarted()==0)
		{
			
			//Notification icon
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);						
			int icon = R.drawable.ic_audio_alarm;
			
			Intent notificationIntent = new Intent(this, TimerServiceActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			CharSequence contentTitle = "PS Timer";  // message title
			CharSequence contentText = model.getName();      // message text
			Notification notification = new Notification();
			notification.icon = icon;
			notification.flags |=Notification.FLAG_NO_CLEAR;
			notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
			
			mNotificationManager.notify(model.getId(), notification);
			
			
			//Defining Counter
			CountDownTimer c = new CountDownTimer((long) model.getTime(),1000)
			{
	
			    @Override
			    public void onFinish() {
				//When the countdown timer finishes
			    	
			    	//Send Broadcast message
			    	finishTimer(timerPosition);
			    	
			    	//Call new Activity to show the alarm screen			    	
			    	Intent dialogIntent = new Intent(getBaseContext(), AlarmSound.class);			    	
			    	Bundle bundle2 = new Bundle();
					bundle2.putInt("timerId", model.getId());					
					dialogIntent.putExtras(bundle2);
					
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					startActivity(dialogIntent);
				}
	
				@Override
				public void onTick(long millisUntilFinished) {
				//When the countdown timer counts 1 second	
					
					//getting Time
					SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
			    	String formatted = format.format(millisUntilFinished); // date is a long in milliseconds
			    				    	
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
					
					//send update Broadcast message
					updateTime(hours, minutes,seconds,timerPosition);					
					
				}
			};
			
			//Starting Counter
			c.start();
			
			//Setting the timer as it has started
			model.setStarted(1);
			databaseHelper.updateTimerById(db, model);
			
			//Saving the timer in the counterList
			countersList[timerPosition] = c;

		}
		
		//if the counter has started
		else
		{
			//Cancelling Notification manager message
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			mNotificationManager.cancel(model.getId());
			
			
			//Getting Counter form the list and cancelling it
			CountDownTimer c = countersList[timerPosition];
			if (c!=null) c.cancel();
						
			//modify database
			model.setStarted(0);
			databaseHelper.updateTimerById(db, model);
			
			//Getting the default time of the timer
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			String formatted = format.format(model.getTime()); // date is a long in milliseconds

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
			
			//Send resteTimer Broadcast Message
			resetTimer(hours, minutes,seconds,timerPosition);
			
		}
		
		db.close();
	}
	
	public static void setMainActivity(TimerServiceActivity activity) {
		mainActivity = activity;
	}
	
	@Override
	public void onDestroy() {
		
		  super.onDestroy();		 
		  
		//Cancel all the timers
		  if (countersList!=null)
		  {			  
			  for (CountDownTimer c: countersList)
			  {
				  if (c!=null) c.cancel();
			  }
		  }
		  
		  DatabaseHelper databaseHelper = new DatabaseHelper(this);
	      SQLiteDatabase db = databaseHelper.getWritableDatabase();
	      
	      ArrayList<Model> allTimers = databaseHelper.selectAllTimers(db);
	      
          db.close();
          
          String ns = Context.NOTIFICATION_SERVICE;
		  NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
          
          for(Model model:allTimers)
          {
        	  mNotificationManager.cancel(model.getId());
          }
		  
		}


	//Update Time BroadCast message
	private void updateTime(int hours, int minutes, int seconds, int position) {
    	 
    	updateTimer.putExtra("hours", hours);
    	updateTimer.putExtra("minutes", minutes);
    	updateTimer.putExtra("seconds", seconds);    	
    	updateTimer.putExtra("position", position);    	
    	sendBroadcast(updateTimer);
    }
	
	//Reset Time BroadCast message
	private void resetTimer(int hours, int minutes, int seconds, int position) {
   	     	
		resetTimer.putExtra("hours", hours);
		resetTimer.putExtra("minutes", minutes);
    	resetTimer.putExtra("seconds", seconds); 
    	resetTimer.putExtra("position", position);    	
    	sendBroadcast(resetTimer);
    }
	
	//Finish Time Broadcast Message
	private void finishTimer(int position) {

    	finishTimer.putExtra("position", position);    	
    	sendBroadcast(finishTimer);
    }


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
