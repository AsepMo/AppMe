package com.appme.story.engine.app.analytics;

import java.util.Calendar;
import java.util.Date;

public class RandomEvent {
	public Date when = Calendar.getInstance().getTime();
	public String value;

	public RandomEvent(String value) {
		this.value = value;
	}
}

