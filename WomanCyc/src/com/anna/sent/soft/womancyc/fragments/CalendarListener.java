package com.anna.sent.soft.womancyc.fragments;

import org.joda.time.LocalDate;

public interface CalendarListener {
	public void showDatePickerToChangeDate();

	public void navigateToDate(LocalDate date);

	public void showDetailedView();

	public void updateDetailedView();
}