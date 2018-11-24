package org.joyinmyzone.wearableDevice.model;

public class Record {
	
	private int userId;
	private int dayId;
	private int hourId;
	private int steps;
	
	public Record(int userId, int dayId, int hourId, int steps) {
		super();
		this.userId = userId;
		this.dayId = dayId;
		this.hourId = hourId;
		this.steps = steps;
	}

	public int getUserId() {
		return userId;
	}

	public int getDayId() {
		return dayId;
	}

	public int getHourId() {
		return hourId;
	}

	public int getSteps() {
		return steps;
	}

	@Override
	public String toString() {
		return "Record [userId=" + userId + ", dayId=" + dayId + ", hourId=" + hourId
				+ ", steps=" + steps + "]";
	}
	
}
