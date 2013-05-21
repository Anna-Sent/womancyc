package com.anna.sent.soft.womancyc.utils;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;

import com.anna.sent.soft.womancyc.data.CalendarData;

public class DateUtils implements Comparator<CalendarData> {
	public static void zeroTime(Calendar date) {
		date.set(Calendar.HOUR, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		date.set(Calendar.AM_PM, Calendar.AM);
	}

	public static String toString(Context context, Calendar date) {
		return DateFormat.getDateFormat(context).format(date.getTime());
	}

	public static boolean datesAreEqual(Calendar date1, Calendar date2) {
		return date1.get(Calendar.DAY_OF_MONTH) == date2
				.get(Calendar.DAY_OF_MONTH)
				&& date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
				&& date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR);
	}

	public static int getDifferenceInDays(Calendar date1, Calendar date2) {
		Calendar tmp1 = (Calendar) date1.clone();
		Calendar tmp2 = (Calendar) date2.clone();
		zeroTime(tmp1);
		zeroTime(tmp2);
		return (int) ((tmp1.getTimeInMillis() - tmp2.getTimeInMillis()) / (3600 * 1000 * 24));
	}

	@Override
	public int compare(CalendarData lhs, CalendarData rhs) {
		Calendar date1 = lhs.getDate();
		Calendar date2 = rhs.getDate();
		if (datesAreEqual(date1, date2)) {
			return 0;
		} else {
			return date1.after(date2) ? 1 : -1;
		}
	}

	public int indexOf(List<CalendarData> values, Calendar value) {
		int index = Collections.binarySearch(values, new CalendarData(value),
				new DateUtils());
		return index;
	}

	public int indexOf(List<CalendarData> values, CalendarData value) {
		int index = Collections.binarySearch(values, value, new DateUtils());
		return index;
	}
}
