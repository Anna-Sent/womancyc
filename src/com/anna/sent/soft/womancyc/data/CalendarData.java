package com.anna.sent.soft.womancyc.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.anna.sent.soft.womancyc.utils.DateUtils;

public class CalendarData implements Serializable {
	private static final long serialVersionUID = -4217182666477849206L;

	private Calendar date;
	private int menstruation = 0;
	private int sex = 0;
	private String note = null;

	public CalendarData() {
		super();
	}

	public CalendarData(Calendar date) {
		super();
		this.date = date;
	}

	public long getId() {
		DateUtils.zeroTime(date);
		return date.getTimeInMillis();
	}

	public Calendar getDate() {
		return date;
	}

	public int getMenstruation() {
		return menstruation;
	}

	public void setMenstruation(int value) {
		menstruation = value;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int value) {
		sex = value;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String value) {
		note = value;
	}

	public void clear() {
		menstruation = 0;
		sex = 0;
		note = null;
	}

	@Override
	public String toString() {
		return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
				.format(date.getTime())
				+ " = "
				+ getId()
				+ ": "
				+ menstruation
				+ ", " + sex + ", \"" + (note == null ? "" : note) + "\"";
	}
}
