package com.anna.sent.soft.womancyc.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.anna.sent.soft.womancyc.utils.DateUtils;

public class CalendarData {
	private Calendar date;
	private long menstruation;
	private long sex;
	private String note;

	public long getId() {
		DateUtils.zeroTime(date);
		return date.getTimeInMillis();
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar value) {
		date = value;
	}

	public long getMenstruation() {
		return menstruation;
	}

	public void setMenstruation(long value) {
		menstruation = value;
	}

	public long getSex() {
		return sex;
	}

	public void setSex(long value) {
		sex = value;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String value) {
		note = value;
	}

	@Override
	public String toString() {
		return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
				.format(date.getTime())
				+ " = "
				+ getId()
				+ ": "
				+ menstruation
				+ ", " + sex + ", " + note;
	}
}
