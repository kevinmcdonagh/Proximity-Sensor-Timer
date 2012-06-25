package com.example;

import android.view.View;

public class Model {

	private String name;
	private float time;
	private boolean selected;
	private String crono;
	private int id;
	private int proximity;
	private int duration;
	private String type;
	private int started;
	private View modelView;
	private int position;

	public String getCrono() {
		return crono;
	}

	public void setCrono(String crono) {
		this.crono = crono;
	}

	public Model(String name, float time, int id, int i, int duration,
			String type, int started) {
		this.id = id;
		this.name = name;
		this.time = time;
		this.proximity = i;
		this.duration = duration;
		this.type = type;
		selected = false;
		this.setStarted(started);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int isProximity() {
		return proximity;
	}

	public void setProximity(int proximity) {
		this.proximity = proximity;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setStarted(int started) {
		this.started = started;
	}

	public int getStarted() {
		return started;
	}

	public void setModelView(View modelView) {
		this.modelView = modelView;
	}

	public View getModelView() {
		return modelView;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

}
