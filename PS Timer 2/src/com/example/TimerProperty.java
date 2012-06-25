package com.example;

public class TimerProperty {

	public static String NAME = "name";
	public static String TIME = "time";
	public static String DURATION = "duration";
	public static String PROXIMITY = "proximity";
	public static String TYPE = "type";	
	private String propertyName;
	private int id;
		
	public TimerProperty(String propertyName, int id) {	
		this.propertyName = propertyName;		
		this.setId(id);
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
