package com.anna.sent.soft.womancyc.fragments;

import java.util.Calendar;

public interface CalendarListener {
	public void showDatePickerToChangeDate();

	public void navigateToDate(Calendar date);

	public void showDetailedView();

	public void updateDetailedView();
}
