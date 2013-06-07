package com.anna.sent.soft.womancyc.data;

import java.io.Serializable;
import java.util.Calendar;

import com.anna.sent.soft.womancyc.utils.DateUtils;

public class CalendarData implements Serializable {
	private static final long serialVersionUID = -4217182666477849206L;

	private Calendar date;
	private int menstruation = 0;
	private int sex = 0;
	private boolean tookPill = false;
	private String note = null;

	public CalendarData() {
		super();
	}

	public CalendarData(Calendar date) {
		super();
		DateUtils.zeroTime(date);
		this.date = date;
	}

	public long getId() {
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

	public boolean getTookPill() {
		return tookPill;
	}

	public void setTookPill(boolean value) {
		tookPill = value;
	}

	public boolean isEmpty() {
		return menstruation == 0 && sex == 0 && !tookPill
				&& (note == null || note.equals(""));
	}

	@Override
	public String toString() {
		return DateUtils.toString(date) + " = " + getId() + ": " + menstruation
				+ ", " + sex + ", " + (tookPill ? "took pill" : " no pill")
				+ ", \"" + (note == null ? "" : note) + "\"";
	}
}
