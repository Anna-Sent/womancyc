package com.anna.sent.soft.womancyc.adapters;

import java.util.Calendar;
import java.util.HashMap;

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

	private static void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private HashMap<Calendar, Calendar> map = new HashMap<Calendar, Calendar>();
	private DataKeeper mDataKeeper;

	public Calculator(DataKeeper dataKeeper) {
		mDataKeeper = dataKeeper;
	}

	public int getDayOfCycle(Calendar current) {
		DateUtils.zeroTime(current);
		int dayOfCycle;
		Calendar firstDayOfCycle = getFirstDayOfCycle(current);
		if (firstDayOfCycle == null) {
			dayOfCycle = 0;
		} else {
			int avgLen = getAverageLengthOfThreeLastMenstrualPeriods(firstDayOfCycle);
			dayOfCycle = DateUtils
					.getDifferenceInDays(current, firstDayOfCycle) % avgLen + 1;
		}

		return dayOfCycle;
	}

	private Calendar getFirstDayOfCycle(Calendar current) {
		DateUtils.zeroTime(current);
		if (map.containsKey(current)) {
			log("get cashed value");
			return map.get(current);
		}

		log("calculate value");
		int currentIndex = mDataKeeper.indexOf(current);
		CalendarData currentData;
		if (currentIndex >= 0) {
			currentData = mDataKeeper.get(currentIndex);
		} else {
			currentIndex = -currentIndex - 2;
			if (currentIndex < 0) {
				map.put(current, null);
				return null;
			}

			currentData = mDataKeeper.get(currentIndex);
		}

		if (map.containsKey(currentData.getDate())
				&& map.get(currentData.getDate()) == null) {
			map.put(current, null);
			return null;
		}

		while (currentData != null && currentData.getMenstruation() == 0) {
			--currentIndex;
			currentData = mDataKeeper.get(currentIndex);
		}

		if (currentData == null) {
			map.put(current, null);
			return null;
		} else {
			CalendarData firstDayOfCycleData;
			Calendar yesterday = (Calendar) currentData.getDate().clone();
			do {
				firstDayOfCycleData = currentData;
				yesterday.add(Calendar.DAY_OF_MONTH, -1);
				currentData = mDataKeeper.get(yesterday);
			} while (currentData != null && currentData.getMenstruation() != 0);

			map.put(current, firstDayOfCycleData.getDate());
			return firstDayOfCycleData.getDate();
		}
	}

	private int getAverageLengthOfThreeLastMenstrualPeriods(
			Calendar firstDayOfCycle) {
		double sum = 0;
		final int count = 3;
		int actualCount = 0;

		while (actualCount < count) {
			Calendar yesterday = (Calendar) firstDayOfCycle.clone();
			yesterday.add(Calendar.DAY_OF_MONTH, -1);

			Calendar firstDayOfPrevCycle = (Calendar) firstDayOfCycle.clone();
			firstDayOfCycle = getFirstDayOfCycle(yesterday);
			if (firstDayOfCycle == null) {
				break;
			}

			sum += DateUtils.getDifferenceInDays(firstDayOfCycle,
					firstDayOfPrevCycle);
			++actualCount;
		}

		return actualCount == 0 ? 28 : (int) Math.round(sum / actualCount);
	}
}
