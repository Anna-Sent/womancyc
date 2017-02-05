package com.anna.sent.soft.womancyc.database;

import com.anna.sent.soft.womancyc.data.CalendarData;

import org.joda.time.LocalDate;

import java.util.List;

public interface DataKeeper {
    CalendarData get(LocalDate date);

    int indexOf(LocalDate date);

    CalendarData get(int index);

    int getCount();

    void insertOrUpdate(CalendarData value);

    void delete(CalendarData value);

    List<String> getNotes();
}
