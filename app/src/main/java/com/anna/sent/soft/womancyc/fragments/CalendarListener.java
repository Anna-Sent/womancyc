package com.anna.sent.soft.womancyc.fragments;

import org.joda.time.LocalDate;

public interface CalendarListener {
    void showDatePickerToChangeDate();

    void navigateToDate(LocalDate date);

    void showDetailedView();

    void updateDetailedView();
}
