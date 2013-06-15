package com.anna.sent.soft.womancyc.database;

import java.util.Calendar;

import com.anna.sent.soft.womancyc.data.CalendarData;

public class CalendarDataManager {
	public static void backup(DataKeeperInterface dataKeeper) {
		for (int i = 0; i < dataKeeper.getCount(); ++i) {
			CalendarData value = dataKeeper.get(i);
		}
	}

	public static void restore(DataKeeperInterface dataKeeper) {
		Calendar date = Calendar.getInstance();
		int menstruation = 0;
		int sex = 0;
		boolean tookPill = true;
		String note = "sdf";
		CalendarData value = new CalendarData(date);
		value.setMenstruation(menstruation);
		value.setSex(sex);
		value.setTookPill(tookPill);
		value.setNote(note);
		dataKeeper.insertOrUpdate(value);
	}
}
