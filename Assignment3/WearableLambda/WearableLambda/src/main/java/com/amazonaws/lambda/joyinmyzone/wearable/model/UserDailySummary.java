package com.amazonaws.lambda.joyinmyzone.wearable.model;

public class UserDailySummary {

	private int userId;
	private int dayId;
	private int steps;
	
	public UserDailySummary(int userId, int dayId, int steps) {
		super();
		this.userId = userId;
		this.dayId = dayId;
		this.steps = steps;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getDayId() {
		return dayId;
	}
	public void setDayId(int dayId) {
		this.dayId = dayId;
	}
	public int getSteps() {
		return steps;
	}
	public void setSteps(int steps) {
		this.steps = steps;
	}
}
