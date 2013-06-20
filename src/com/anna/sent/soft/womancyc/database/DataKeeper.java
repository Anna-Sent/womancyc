package com.anna.sent.soft.womancyc.database;

import java.util.Calendar;
import java.util.List;

import com.anna.sent.soft.womancyc.data.CalendarData;

public interface DataKeeper {
	public CalendarData get(Calendar date);

	public int indexOf(Calendar date);

	public CalendarData get(int index);

	public int getCount();

	public void insertOrUpdate(CalendarData value);

	public void delete(CalendarData value);

	public List<String> getNotes();
}
