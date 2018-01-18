package spiced.tea.cup.time;

import java.time.LocalTime;

public class TeaOrder {

	private String orderName;
	// private String stringTeaTime;
	private LocalTime teaTime;

	// add transient to variables you don't want serialized
	// during the json writing
	// https://github.com/google/gson/blob/master/UserGuide.md#TOC-Excluding-Fields-From-Serialization-and-Deserialization
	// for more info

	public TeaOrder() {

	}

	public TeaOrder(String orderName, LocalTime teaTime) {
		this.orderName = orderName;
		this.teaTime = teaTime;
	}

	public TeaOrder(String orderName, String teaTimeString) {
		this.orderName = orderName;
		this.teaTime = LocalTime.parse(teaTimeString);
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public void setTeaTime(String teaTime) {
		this.teaTime = LocalTime.parse(teaTime);
	}

	public int getTeaTimeHour() {
		return teaTime.getHour();

	}

	public int getTeaTimeMinute() {
		return teaTime.getMinute();
	}

	public String getOrderName() {
		return orderName;
	}

	public LocalTime getTeaTime() {
		return teaTime;
	}

}
