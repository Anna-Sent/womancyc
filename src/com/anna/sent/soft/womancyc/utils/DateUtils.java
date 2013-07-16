package com.anna.sent.soft.womancyc.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.text.format.DateFormat;

public class DateUtils {
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

	public static String toString(Calendar date) {
		return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
				.format(date.getTime());
	}

	public static boolean datesAreEqual(Calendar date1, Calendar date2) {
		int d1 = date1.get(Calendar.DAY_OF_MONTH);
		int d2 = date2.get(Calendar.DAY_OF_MONTH);
		int m1 = date1.get(Calendar.MONTH);
		int m2 = date2.get(Calendar.MONTH);
		int y1 = date1.get(Calendar.YEAR);
		int y2 = date2.get(Calendar.YEAR);
		return d1 == d2 && m1 == m2 && y1 == y2;
	}

	public static boolean before(Calendar date1, Calendar date2) {
		int d1 = date1.get(Calendar.DAY_OF_MONTH);
		int d2 = date2.get(Calendar.DAY_OF_MONTH);
		int m1 = date1.get(Calendar.MONTH);
		int m2 = date2.get(Calendar.MONTH);
		int y1 = date1.get(Calendar.YEAR);
		int y2 = date2.get(Calendar.YEAR);
		if (y1 == y2) {
			if (m1 == m2) {
				if (d1 == d2) {
					return false;
				} else {
					return d1 < d2;
				}
			} else {
				return m1 < m2;
			}
		} else {
			return y1 < y2;
		}
	}

	public static int compare(Calendar date1, Calendar date2) {
		int d1 = date1.get(Calendar.DAY_OF_MONTH);
		int d2 = date2.get(Calendar.DAY_OF_MONTH);
		int m1 = date1.get(Calendar.MONTH);
		int m2 = date2.get(Calendar.MONTH);
		int y1 = date1.get(Calendar.YEAR);
		int y2 = date2.get(Calendar.YEAR);
		if (y1 == y2) {
			if (m1 == m2) {
				if (d1 == d2) {
					return 0;
				} else {
					return d1 > d2 ? 1 : -1;
				}
			} else {
				return m1 > m2 ? 1 : -1;
			}
		} else {
			return y1 > y2 ? 1 : -1;
		}
	}

	public static boolean beforeOrEqual(Calendar date1, Calendar date2) {
		int d1 = date1.get(Calendar.DAY_OF_MONTH);
		int d2 = date2.get(Calendar.DAY_OF_MONTH);
		int m1 = date1.get(Calendar.MONTH);
		int m2 = date2.get(Calendar.MONTH);
		int y1 = date1.get(Calendar.YEAR);
		int y2 = date2.get(Calendar.YEAR);
		if (y1 == y2) {
			if (m1 == m2) {
				if (d1 == d2) {
					return true;
				} else {
					return d1 < d2;
				}
			} else {
				return m1 < m2;
			}
		} else {
			return y1 < y2;
		}
	}

	public static boolean after(Calendar date1, Calendar date2) {
		int d1 = date1.get(Calendar.DAY_OF_MONTH);
		int d2 = date2.get(Calendar.DAY_OF_MONTH);
		int m1 = date1.get(Calendar.MONTH);
		int m2 = date2.get(Calendar.MONTH);
		int y1 = date1.get(Calendar.YEAR);
		int y2 = date2.get(Calendar.YEAR);
		if (y1 == y2) {
			if (m1 == m2) {
				if (d1 == d2) {
					return false;
				} else {
					return d1 > d2;
				}
			} else {
				return m1 > m2;
			}
		} else {
			return y1 > y2;
		}
	}

	public static boolean afterOrEqual(Calendar date1, Calendar date2) {
		int d1 = date1.get(Calendar.DAY_OF_MONTH);
		int d2 = date2.get(Calendar.DAY_OF_MONTH);
		int m1 = date1.get(Calendar.MONTH);
		int m2 = date2.get(Calendar.MONTH);
		int y1 = date1.get(Calendar.YEAR);
		int y2 = date2.get(Calendar.YEAR);
		if (y1 == y2) {
			if (m1 == m2) {
				if (d1 == d2) {
					return true;
				} else {
					return d1 > d2;
				}
			} else {
				return m1 > m2;
			}
		} else {
			return y1 > y2;
		}
	}

	public static int getDifferenceInDays(Calendar date1, Calendar date2) {
		Calendar tmp1 = (Calendar) date1.clone();
		Calendar tmp2 = (Calendar) date2.clone();
		zeroTime(tmp1);
		zeroTime(tmp2);
		return (int) ((tmp1.getTimeInMillis() - tmp2.getTimeInMillis()) / (3600 * 1000 * 24));
	}
}
