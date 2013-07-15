package com.anna.sent.soft.womancyc.data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

import com.anna.sent.soft.womancyc.utils.DateUtils;

public class CalendarData implements Serializable {
	private static final String DATE_FORMAT = "dd-MM-yyyy";
	private static final long serialVersionUID = -4217182666477849206L;

	private Calendar date;
	private int menstruation = 0;
	private int sex = 0;
	private boolean tookPill = false;
	private String note = null;

	public CalendarData() {
		this(Calendar.getInstance());
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

	@SuppressLint("SimpleDateFormat")
	public String getDateString() {
		return new SimpleDateFormat(DATE_FORMAT).format(date.getTime());
	}

	@SuppressLint("SimpleDateFormat")
	public void setDate(String value) {
		try {
			Date date = new SimpleDateFormat(DATE_FORMAT).parse(value);
			this.date.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public int getMenstruation() {
		return menstruation;
	}

	private static final String MENSTRUATION_YES = "yes";
	private static final String MENSTRUATION_ONE_DROP = "one_drop";
	private static final String MENSTRUATION_TWO_DROPS = "two_drops";
	private static final String MENSTRUATION_THREE_DROPS = "three_drops";
	private static final String MENSTRUATION_NO = "no";

	public String getMenstruationString() {
		switch (menstruation) {
		case 1:
			return MENSTRUATION_YES;
		case 2:
			return MENSTRUATION_ONE_DROP;
		case 3:
			return MENSTRUATION_TWO_DROPS;
		case 4:
			return MENSTRUATION_THREE_DROPS;
		}

		return MENSTRUATION_NO;
	}

	public void setMenstruation(int value) {
		menstruation = value;
	}

	public void setMenstruation(String value) {
		if (value.equals(MENSTRUATION_YES)) {
			menstruation = 1;
		} else if (value.equals(MENSTRUATION_ONE_DROP)) {
			menstruation = 2;
		} else if (value.equals(MENSTRUATION_TWO_DROPS)) {
			menstruation = 3;
		} else if (value.equals(MENSTRUATION_THREE_DROPS)) {
			menstruation = 4;
		} else {
			menstruation = 0;
		}
	}

	public int getSex() {
		return sex;
	}

	private static final String SEX_UNPROTECTED = "unprotected";
	private static final String SEX_PROTECTED = "protected";
	private static final String SEX_YES = "yes";
	private static final String SEX_NO = "no";

	public String getSexString() {
		switch (sex) {
		case 1:
			return SEX_UNPROTECTED;
		case 2:
			return SEX_PROTECTED;
		case 3:
			return SEX_YES;
		}

		return SEX_NO;
	}

	public void setSex(int value) {
		sex = value;
	}

	public void setSex(String value) {
		if (value.equals(SEX_UNPROTECTED)) {
			sex = 1;
		} else if (value.equals(SEX_PROTECTED)) {
			sex = 2;
		} else if (value.equals(SEX_YES)) {
			sex = 3;
		} else {
			sex = 0;
		}
	}

	public String getNote() {
		return note == null ? "" : note;
	}

	public void setNote(String value) {
		note = value;
	}

	public boolean getTookPill() {
		return tookPill;
	}

	private final static String TOOK_PILL_YES = "yes";
	private final static String TOOK_PILL_NO = "no";

	public String getTookPillString() {
		return tookPill ? TOOK_PILL_YES : TOOK_PILL_NO;
	}

	public void setTookPill(boolean value) {
		tookPill = value;
	}

	public void setTookPill(String value) {
		tookPill = value.equals(TOOK_PILL_YES);
	}

	public boolean isEmpty() {
		return menstruation == 0 && sex == 0 && !tookPill
				&& (note == null || note.equals(""));
	}

	@Override
	public String toString() {
		return getDateString() + ":: \n" + "\tmenstruation: "
				+ getMenstruationString() + ", \n" + "\tsex: " + getSexString()
				+ ", \n" + "\ttook pill: " + getTookPillString() + ", \n"
				+ "note: \"" + (note == null ? "" : note) + "\";\n";
	}
}
