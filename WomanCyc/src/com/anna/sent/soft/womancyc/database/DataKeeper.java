package com.anna.sent.soft.womancyc.database;

import java.util.List;

import org.joda.time.LocalDate;

import com.anna.sent.soft.womancyc.data.CalendarData;

public interface DataKeeper {
	public CalendarData get(LocalDate date);

	public int indexOf(LocalDate date);

	public CalendarData get(int index);

	public int getCount();

	public void insertOrUpdate(CalendarData value);

	public void delete(CalendarData value);

	public List<String> getNotes();
}