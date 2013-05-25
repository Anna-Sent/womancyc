package com.anna.sent.soft.womancyc.data;

import java.util.List;


public interface DataKeeper {
	public List<CalendarData> getData();

	public void insertOrUpdate(CalendarData value);

	public void delete(CalendarData value);

	public void cancel(CalendarData value);

	public List<String> getNotes();
}
