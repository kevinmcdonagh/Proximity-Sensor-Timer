package com.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "MyTimers74.db";
	public static final String TIMER_NAME = "name";
	public static final String TABLE_NAME = "timers";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 3);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		// Create Table
		db.execSQL("CREATE TABLE "+ TABLE_NAME+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, time TEXT, proximity BOOLEAN, duration INT, type TEXT, started BOOLEAN);");

		// Insert Sample Data
		db.execSQL("INSERT INTO " + TABLE_NAME+ " VALUES (0, 'Example Timer','00:01:00', 0, 10, 'Sound', 0);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "");
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, time TEXT, proximity BOOLEAN, duration INT, type TEXT, started BOOLEAN);");
	}

	//Get Timer By Id
	public Model selectModelById(SQLiteDatabase db, int model_id) {

		Model timer = null;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

		Cursor cursor = db.query(TABLE_NAME, null, "id=" + model_id, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				String formatted = cursor.getString(2);
				Date date = null;
				try {
					date = format.parse(formatted);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				timer = new Model(cursor.getString(1), date.getTime(),
						cursor.getInt(0), cursor.getInt(3), cursor.getInt(4),
						cursor.getString(5), cursor.getInt(6));

			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return timer;
	}

	
	//Get all the timers
	public ArrayList<Model> selectAllTimers(SQLiteDatabase db) {

		ArrayList<Model> timersList = new ArrayList<Model>();

		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				String formatted = cursor.getString(2);
				TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

				Date date = null;
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

				try {

					date = (Date) format.parse(formatted);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Model timer = new Model(cursor.getString(1), date.getTime(),
						cursor.getInt(0), cursor.getInt(3), cursor.getInt(4),
						cursor.getString(5), cursor.getInt(6));

				timersList.add(timer);

			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return timersList;
	}

	public void deleteTimerById(SQLiteDatabase db, int id) {
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE id=" + id);
	}

	public void updateTimerById(SQLiteDatabase db, Model model) {

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String formatted = format.format(model.getTime());

		db.execSQL("UPDATE " + TABLE_NAME + " SET name ='" + model.getName()
				+ "', time = '" + formatted + "', proximity = "
				+ model.isProximity() + ", type = '" + model.getType()
				+ "', started = " + model.getStarted() + "  WHERE id="
				+ model.getId());
	}

	
	//Reset all the timers
	public void restartAllTimers(SQLiteDatabase db) {
		
		ArrayList<Model> timersList = new ArrayList<Model>();

		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				String formatted = cursor.getString(2);
				TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

				Date date = null;
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

				try {

					date = (Date) format.parse(formatted);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Model timer = new Model(cursor.getString(1), date.getTime(),
						cursor.getInt(0), cursor.getInt(3), cursor.getInt(4),
						cursor.getString(5), cursor.getInt(6));

				timersList.add(timer);

			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		//All the timers of the database are saved in timersList
		//Now let's update the time
		for (Model model2 : timersList) {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			String formatted = format.format(model2.getTime());

			db.execSQL("UPDATE " + TABLE_NAME + " SET name ='"
					+ model2.getName() + "', time = '" + formatted
					+ "', proximity = " + model2.isProximity() + ", type = '"
					+ model2.getType() + "', started = 0  WHERE id="
					+ model2.getId());
		}

	}

	
	//Returns true if there is any active timer
	public boolean isAnyTimerActive(SQLiteDatabase db) {
		
		int alarmsActive = 0;
		
		ArrayList<Model> timersList = new ArrayList<Model>();

		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				String formatted = cursor.getString(2);
				TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

				Date date = null;
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

				try {

					date = (Date) format.parse(formatted);

				} catch (ParseException e) {
					
					e.printStackTrace();
				}

				Model timer = new Model(cursor.getString(1), date.getTime(),
						cursor.getInt(0), cursor.getInt(3), cursor.getInt(4),
						cursor.getString(5), cursor.getInt(6));

				timersList.add(timer);

			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		for (Model model2 : timersList) {
			if (model2.getStarted() == 1)
				alarmsActive++;

		}

		if (alarmsActive > 0)
			return true;
		else
			return false;
	}

}
