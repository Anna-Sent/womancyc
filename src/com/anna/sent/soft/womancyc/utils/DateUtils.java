package com.anna.sent.soft.womancyc.utils;

import java.util.Calendar;

import android.content.Context;
import android.text.format.DateFormat;

public class DateUtils {
	public static void zeroDate(Calendar date) {
		date.set(Calendar.HOUR, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		date.set(Calendar.AM_PM, Calendar.AM);
	}

	public static String toString(Context context, Calendar date) {
		return DateFormat.getDateFormat(context).format(date.getTime());
	}
}
