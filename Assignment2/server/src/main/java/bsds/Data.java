package bsds;

public class Data {
	
	private int id;
	private int userId;
	private int day;
	private int hour;
	private int stepCount;

	public Data(int id, int userId, int day, int hour, int stepCount) {
		this.id = id;
		this.userId = userId;
		this.day = day;
		this.hour = hour;
		this.stepCount = stepCount;
	}

	public Data(int userId, int day, int hour, int stepCount) {
		this.userId = userId;
		this.day = day;
		this.hour = hour;
		this.stepCount = stepCount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getStepCount() {
		return stepCount;
	}

	public void setStepCount(int stepCount) {
		this.stepCount = stepCount;
	}

	@Override
	public String toString() {
		return "Data{" +
				"id=" + id +
				", userId=" + userId +
				", day=" + day +
				", hour=" + hour +
				", stepCount=" + stepCount +
				'}';
	}
}
