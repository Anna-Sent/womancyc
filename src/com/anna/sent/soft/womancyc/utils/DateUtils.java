package com.anna.sent.soft.womancyc.utils;

import java.util.Calendar;

import android.content.Context;
import android.text.format.DateFormat;

public class DateUtils {
	private static void zeroDate(Calendar date) {
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
		zeroDate(tmp1);
		zeroDate(tmp2);
		return (int) ((tmp1.getTimeInMillis() - tmp2.getTimeInMillis()) / (3600 * 1000 * 24));
	}
}
