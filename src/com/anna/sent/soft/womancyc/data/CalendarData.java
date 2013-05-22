package com.anna.sent.soft.womancyc.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.anna.sent.soft.womancyc.utils.DateUtils;

public class CalendarData implements Serializable {
	private static final long serialVersionUID = -4217182666477849206L;

	private Calendar date;
	private long menstruation = 0;
	private long sex = 0;
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
				+ ", " + sex + ", \"" + (note == null ? "" : note) + "\"";
	}
}
