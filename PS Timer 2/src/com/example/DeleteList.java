package com.example;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class DeleteList extends Activity {

	private static String TAG = "DeleteList";
	private ListView listView;

	public void onCreate(Bundle icicle) {
		
		super.onCreate(icicle);		
		setContentView(R.layout.deletemain);
		
		ArrayAdapter<Model> adapter = new DeleteAdapter(this, R.layout.deleterow, getAllTimers());	
		listView = (ListView)findViewById(R.id.list);		
		listView.setAdapter(adapter);
				
		//Select All CheckBox
		CheckBox checkbox = (CheckBox) findViewById(R.id.checkAll);
		checkbox.setChecked(false);
		Button myButton = (Button)findViewById(R.id.delete);
		myButton.setEnabled(false);
				
		//Select All CheckBox Behaviour
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				
				if(isChecked)
				{		
					//If the user checks it all the checkboxs will be checked
					for(int i=0; i < listView.getChildCount(); i++){
						RelativeLayout itemLayout = (RelativeLayout)listView.getChildAt(i);
						CheckBox cb = (CheckBox)itemLayout.findViewById(R.id.check);
						cb.setChecked(true);
					}
					Button myButton = (Button)findViewById(R.id.delete);
					myButton.setEnabled(true);
				}
				
				else
				{
					//check how many checks are checked
					Log.d(TAG,"not checked: "+listView.getChildCount());
					int checkedTimers=0;
					for(int i=0; i < listView.getChildCount(); i++){
						RelativeLayout itemLayout = (RelativeLayout)listView.getChildAt(i);
						CheckBox cb = (CheckBox)itemLayout.findViewById(R.id.check);
						if(cb.isChecked())
						{
							checkedTimers++;
						}

					}
					
					//If all the checks are checked then uncheck them
					if(checkedTimers==listView.getCount())
					{
						for(int i=0; i < listView.getChildCount(); i++){
							RelativeLayout itemLayout = (RelativeLayout)listView.getChildAt(i);
							CheckBox cb = (CheckBox)itemLayout.findViewById(R.id.check);
							cb.setChecked(false);
						}
						
						Button myButton = (Button)findViewById(R.id.delete);
						myButton.setEnabled(false);
					}	
					
					
				}
			}
		});
			
		
		//Cancel Button						
		Button cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new Button.OnClickListener(){ 
		    
	        public void onClick(View v) {
	        	finish();         
	        }
		});
		
						 
		//delete Button		
		Button deletelButton = (Button) findViewById(R.id.delete);
		
		final DatabaseHelper databaseHelper = new DatabaseHelper(this);
					
		deletelButton.setOnClickListener(new Button.OnClickListener(){ 
		    
	        public void onClick(View v) {
	        	
	        	//open DB
	    		final SQLiteDatabase db = databaseHelper.getWritableDatabase();		
	    		final boolean anyTimerStarted=databaseHelper.isAnyTimerActive(db);
	        	
	        	//if there is any Timer Started
	        	if (anyTimerStarted)
				{
	        		//Create Dialog showing that there are Timers Running and they will be reset
					AlertDialog.Builder builder = new AlertDialog.Builder(DeleteList.this);
			    	 
			    	builder.setMessage("All the timers will be reinitialized after this action. Do you want to proceed?")
			    	        .setCancelable(false)
			    	        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			    	            
			    	        	//If the user wants to continue
			    	        	public void onClick(DialogInterface dialog, int id) {
			    	            	
			    	            	//Get items selected
			    	        		final ArrayList<Integer> selectedItems;
			    		        	selectedItems = new ArrayList<Integer>();
			    		        	
			    		        	for(int i=0;i<getAllTimers().size();i++)
			    		        	{
			    		        		Model model = (Model) listView.getAdapter().getItem(i);
			    		        		if (model.isSelected())
			    		        		{
			    		        			selectedItems.add(model.getId());
			    		        		}
			    		        	}	
			    	                
			    		        	//erase from DB
			    	            	for(Integer integerarray: selectedItems)
			    		        	{
			    		        		databaseHelper.deleteTimerById(db, integerarray);
			    		        	}
			    		        	
			    		        	db.close();		
			    		        	
			    		        	//Show a message "Timers Deleted"
			    		        	if (selectedItems.size()==1)	Toast.makeText(getBaseContext(), "Timer Deleted", Toast.LENGTH_LONG).show();
			    		        	else Toast.makeText(getBaseContext(), "Timers Deleted", Toast.LENGTH_LONG).show();

			    		        	//Return to main Activity
			    		            Intent intent= new Intent();	    	            
			    		    		intent.setComponent(new ComponentName( getBaseContext(), TimerServiceActivity.class));	    	    		
			    		    		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			    		    		startActivity(intent);
			    		    		overridePendingTransition(R.anim.push_up_out,R.anim.push_left_in);
			    		    		
			    		    		stopService(new Intent(DeleteList.this,TimerService.class));
			    	            }
			    	        })
			    	        
			    	        //Cancel Button
			    	        .setNegativeButton("No", new DialogInterface.OnClickListener() {
			    	            public void onClick(DialogInterface dialog, int id) {
			    	                 dialog.cancel();
			    	            }
			    	        })
			    	        .setIcon(R.drawable.alert_dialog_icon)
			    	        .setTitle("Warning");
			    	 		
			    	        ;
				    	 AlertDialog alert = builder.create();
				    	 alert.show();
					}
					
	        		// if there are no Timers started 
					else
					{
						//Get Selected Items
						ArrayList<Integer> selectedItems;
			        	selectedItems = new ArrayList<Integer>();
			        	
			        	for(int i=0;i<getAllTimers().size();i++)
			        	{
			        		Model model = (Model) listView.getAdapter().getItem(i);
			        		if (model.isSelected())
			        		{
			        			selectedItems.add(model.getId());
			        		}
			        	}
			        	
			        	//erase from DB
			        	for(Integer integerarray: selectedItems)
			        	{
			        		databaseHelper.deleteTimerById(db, integerarray);
			        	}
			        	
			        	db.close();		
			        	
			        	//Show a message "Timers Deleted"
			        	if (selectedItems.size()==1)	Toast.makeText(getBaseContext(), "Timer Deleted", Toast.LENGTH_LONG).show();
			        	else Toast.makeText(getBaseContext(), "Timers Deleted", Toast.LENGTH_LONG).show();
		
			        	//Return to main Activity
			            Intent intent= new Intent();	    	            
			    		intent.setComponent(new ComponentName( getBaseContext(), TimerServiceActivity.class));	    	    		
			    		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			    		startActivity(intent);
			    		overridePendingTransition(R.anim.push_up_out,R.anim.push_left_in);
			    		
			        }
	        }});
        
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
        
        else {
        	return null;
        }   	   	
    
    }
	
	
	public ListView getListView()
	{
		return listView;
	}
	
}
