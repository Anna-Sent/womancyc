package com.anna.sent.soft.womancyc.adapters;

import java.util.Calendar;

import android.util.Log;

import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public class Calculator {
	private static final String TAG = "moo";
	private static final boolean DEBUG = true;

	private static String wrapMsg(String msg) {
		return Calculator.class.getSimpleName() + ": " + msg;
	}

	@SuppressWarnings("unused")
	private static void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	public static int getDayOfCycle(Calendar current, DataKeeper dataKeeper) {
		int currentIndex = dataKeeper.indexOf(current);
		CalendarData currentData;
		if (currentIndex >= 0) {
			currentData = dataKeeper.get(currentIndex);
		} else {
			currentIndex = -currentIndex - 2;
			if (currentIndex < 0) {
				return 0;
			}

			currentData = dataKeeper.get(currentIndex);
		}

		while (currentData != null && currentData.getMenstruation() == 0) {
			--currentIndex;
			currentData = dataKeeper.get(currentIndex);
		}

		if (currentData == null) {
			return 0;
		} else {
			CalendarData firstDayOfCycle;
			Calendar yesterday = (Calendar) currentData.getDate().clone();
			do {
				firstDayOfCycle = currentData;
				yesterday.add(Calendar.DAY_OF_MONTH, -1);
				currentData = dataKeeper.get(yesterday);
			} while (currentData != null && currentData.getMenstruation() != 0);

			int dayOfCycle = DateUtils.getDifferenceInDays(current,
					firstDayOfCycle.getDate()) + 1;
			return dayOfCycle;
		}
	}
}
