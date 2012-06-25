package com.example;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import com.google.ads.AdRequest;
//import com.google.ads.AdSize;
//import com.google.ads.AdView;

public class TimerServiceActivity extends Activity {
	
	private static String TAG = "TimerServiceActivity";
	
	private static ArrayAdapter<Model> adapter;
//	private AdView adView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.UPDATE_TIMER));
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.RESET_TIMER));
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.FINISH_TIMER));
     
        //ADVERTISEMENTS
//		adView = (AdView)this.findViewById(R.id.adView);
//		adView.loadAd(new AdRequest());
//		AdRequest adRequest = new AdRequest();
//		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
//		// Initiate a generic request to load it with an ad
//		adView.loadAd(adRequest);	    
//		
//		// Create the adView
//	    adView = new AdView(this, AdSize.BANNER, 3333);
//	    AdView ad = (AdView)findViewById(R.id.ad);
//	    ad.setVisibility(AdView.VISIBLE);
	          
        //Reseting all the timers
        resetTimers();
        
        ImageView image = (ImageView) findViewById(R.id.image);  
        
        //Pressing the Add image
		image.setOnTouchListener(new OnTouchListener() {		   
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					
					ArrayList<Model> alltimers = getAllTimers();
					
					//limited no more than 4 timers
					if (alltimers.size()>3)
					{
						LayoutInflater inflater = getLayoutInflater();
						View layout = inflater.inflate(R.layout.toast_layout,(ViewGroup) findViewById(R.id.toast_layout_root));
						
						TextView text = (TextView) layout.findViewById(R.id.text);
						text.setText("You have reached the maximum number of Timers");

						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.setDuration(Toast.LENGTH_LONG);
						toast.setView(layout);
						toast.show();						
					}
					
					//If the user has not more than 3 timers
					else
					{
						Intent start = new Intent();
						start.setComponent(new ComponentName(TimerServiceActivity.this,NewTimer.class));
						startActivity(start);
					}
				}
				
				return true;				

			}
		});
        
		//Setting this activity to the Service Time Service
        try{
            TimerService.setMainActivity(TimerServiceActivity.this);
       }catch(Exception e){        	   
    	   e.printStackTrace();
       }
		
       adapter = new TimersListArrayAdapter(this, getAllTimers());
       final ListView listView = (ListView)findViewById(R.id.list);
       listView.setAdapter(adapter);
	
       //If the user selects a timer then go to edit the timer	
       listView.setOnItemClickListener(new OnItemClickListener() {
		    
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {				

					Model selectedTimer = (Model) listView.getAdapter().getItem(position);

			    	Bundle bundle = new Bundle();
					bundle.putInt("timerId", selectedTimer.getId());
						
					Intent start = new Intent(TimerServiceActivity.this, NewTimer.class);
					start.putExtras(bundle);
					startActivity(start);				
		    }
       });
		
    }
    
    
    public ArrayList<Model> getAllTimers(){
    	  	
    	DatabaseHelper databaseHelper = new DatabaseHelper(this);
    	SQLiteDatabase db = databaseHelper.getWritableDatabase();
        
        if(db != null)
        {
        	ArrayList<Model> allTimers = databaseHelper.selectAllTimers(db);        	
            db.close();            
            return allTimers;
        }
        
        else { return null; }   	

    }
    
    //Creating Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {    
		
		// Handle item selection
		switch (item.getItemId()) {    
			case R.id.newTimer:	

				ArrayList<Model> alltimers = getAllTimers();
				
				//limited no more than 4 timers
				if (alltimers.size()>3)
				{
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.toast_layout,(ViewGroup) findViewById(R.id.toast_layout_root));
					
					TextView text = (TextView) layout.findViewById(R.id.text);
					text.setText("You have reached the maximum number of Timers");

					Toast toast = new Toast(getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.setView(layout);
					toast.show();
				}
				
				//If the user has not more than 3 timers
				else
				{
					Intent start = new Intent();
					start.setComponent(new ComponentName(TimerServiceActivity.this, NewTimer.class));
					startActivity(start);
				}				
		       
				return true;
				
			case R.id.delete:
				
				//call Deletelist Activity
            	Intent intent2= new Intent();
	    		intent2.setComponent(new ComponentName(TimerServiceActivity.this, DeleteList.class));
	    		startActivity(intent2);	    
	    		
				return true; 
						
			default:
				return false;
		}
		
 
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		//Overriding Back key
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setMessage("Are you sure you want to exit?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							finish();
							stopService(new Intent(TimerServiceActivity.this, TimerService.class));
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							dialog.cancel();
						}
					}).setIcon(R.drawable.alert_dialog_icon)
					.setTitle("Exit");

			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	//Reset timers
	public void resetTimers(){
    	
    	DatabaseHelper databaseHelper = new DatabaseHelper(this);
    	SQLiteDatabase db = databaseHelper.getWritableDatabase();        
    	databaseHelper.restartAllTimers(db);
        db.close();
        
    }
	
	//Define BroadCast receiver
	public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	//If the activity receives an update message from the Service        
        	if (intent.getAction().equals(TimerService.UPDATE_TIMER))
        	{   
        		//Update UI
        		
        		int hours = intent.getIntExtra("hours",-1); 
            	int minutes = intent.getIntExtra("minutes",-1);
            	int seconds = intent.getIntExtra("seconds",-1); 
            	int position = intent.getIntExtra("position",-1);
            	
//            	Log.d(TAG, "Updating Time Position: "+position);
            	
            	ListView listView2 = (ListView)findViewById(R.id.list);
            	
            	int firstPosition = listView2.getFirstVisiblePosition() - listView2.getHeaderViewsCount();
            	
            	if(position>=firstPosition && position<firstPosition+5)
            	{
            		View actualView = listView2.getChildAt(position-firstPosition);
            		
            		LinearLayout linearLayout = (LinearLayout) actualView.findViewById(R.id.linear);
            		linearLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
                	
                	TextView cronohours = (TextView) actualView.findViewById(R.id.cronohours);
                	TextView cronominutes = (TextView) actualView.findViewById(R.id.cronominutes);
                	TextView cronoseconds = (TextView) actualView.findViewById(R.id.cronoseconds);
                	
    				if (hours<10) cronohours.setText("0"+hours);
    				else cronohours.setText(""+hours);
    				
    				if (minutes<10) cronominutes.setText("0"+minutes);
    				else cronominutes.setText(""+minutes);
    				
    				if (seconds<10) cronoseconds.setText("0"+seconds);
    				else cronoseconds.setText(""+seconds);
                	        	       		
            	}
        	}
        	
        	//If the activity receives an reset timer message from the Service  
        	else if (intent.getAction().equals(TimerService.RESET_TIMER))
        	{
        		
        		//Update UI
        		
        		Log.d(TAG, "Stopping Timer");
        		
        		int hours = intent.getIntExtra("hours",-1); 
            	int minutes = intent.getIntExtra("minutes",-1);
            	int seconds = intent.getIntExtra("seconds",-1); 
            	int position = intent.getIntExtra("position",-1);
            	
            	
            	ListView listView2 = (ListView)findViewById(R.id.list);
            	
            	int firstPosition = listView2.getFirstVisiblePosition() - listView2.getHeaderViewsCount();
            	
            	if(position>=firstPosition && position<firstPosition+5)
            	{
            		View actualView = listView2.getChildAt(position-firstPosition);
            		
            		LinearLayout linearLayout = (LinearLayout) actualView.findViewById(R.id.linear);
            		linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                	
                	TextView cronohours = (TextView) actualView.findViewById(R.id.cronohours);
                	TextView cronominutes = (TextView) actualView.findViewById(R.id.cronominutes);
                	TextView cronoseconds = (TextView) actualView.findViewById(R.id.cronoseconds);
                	
    				if (hours<10) cronohours.setText("0"+hours);
    				else cronohours.setText(""+hours);
    				
    				if (minutes<10) cronominutes.setText("0"+minutes);
    				else cronominutes.setText(""+minutes);
    				
    				if (seconds<10) cronoseconds.setText("0"+seconds);
    				else cronoseconds.setText(""+seconds);
                	        	       		
            	}
        		
        	}
        	
        	//If the activity receives a finishing timer message from the Service  
        	else if (intent.getAction().equals(TimerService.FINISH_TIMER))
        	{        
        		//Update UI
        		
        		Log.d(TAG, "Finishing Timer");        		
        	
            	int position = intent.getIntExtra("position",-1);
            	
            	ListView listView2 = (ListView)findViewById(R.id.list);
            	int firstPosition = listView2.getFirstVisiblePosition() - listView2.getHeaderViewsCount();
            	View actualView = listView2.getChildAt(position-firstPosition);
            	CheckBox checkBox = (CheckBox) actualView.findViewById(R.id.check);
            	checkBox.setChecked(false);
            	
        	}
        }
    };
    
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
    
}