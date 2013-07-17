package com.anna.sent.soft.womancyc.fragments;

import java.util.Calendar;

public interface CalendarListener {
	public void showDatePickerToChangeDate();

	public void showDate(Calendar date);

	public void showDetailedView(Calendar date);
}
