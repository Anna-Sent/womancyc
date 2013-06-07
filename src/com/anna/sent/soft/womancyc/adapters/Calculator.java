package com.anna.sent.soft.womancyc.adapters;

import java.util.Calendar;
import java.util.HashMap;

import android.util.Log;

import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.DataKeeperInterface;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public class Calculator {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private static String wrapMsg(String msg) {
		return Calculator.class.getSimpleName() + ": " + msg;
	}

	private static void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private HashMap<Calendar, Calendar> map = new HashMap<Calendar, Calendar>();
	private DataKeeperInterface mDataKeeper;

	public Calculator(DataKeeperInterface dataKeeper) {
		mDataKeeper = dataKeeper;
	}

	public int getDayOfCycle(Calendar current) {
		DateUtils.zeroTime(current);
		int dayOfCycle;
		Calendar firstDayOfCycle = getFirstDayOfCycle(current);
		if (firstDayOfCycle == null) {
			dayOfCycle = 0;
		} else {
			int nextIndex = getLeftNeighborIndex(current);
			CalendarData nextData;
			do {
				++nextIndex;
				nextData = mDataKeeper.get(nextIndex);
			} while (nextData != null && nextData.getMenstruation() == 0);

			if (nextData == null) {
				int avgLen = getAvgLenOfLastMenstrualCycles(firstDayOfCycle);
				dayOfCycle = DateUtils.getDifferenceInDays(current,
						firstDayOfCycle) % avgLen + 1;
			} else {
				dayOfCycle = DateUtils.getDifferenceInDays(current,
						firstDayOfCycle) + 1;
			}
		}

		return dayOfCycle;
	}

	private int getLeftNeighborIndex(Calendar current) {
		int currentIndex = mDataKeeper.indexOf(current);
		if (currentIndex >= 0) {
			return currentIndex;
		} else {
			return -currentIndex - 2;
		}
	}

	public Calendar getFirstDayOfCycle(Calendar current) {
		DateUtils.zeroTime(current);
		if (map.containsKey(current)) {
			log("get cashed value");
			return map.get(current);
		}

		log("calculate value");
		int currentIndex = getLeftNeighborIndex(current);
		CalendarData currentData = mDataKeeper.get(currentIndex);

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

	private int getAvgLenOfLastMenstrualCycles(Calendar firstDayOfCycle) {
		double sum = 0;
		final int count = 3;
		int actualCount = 0;
		int countOfCycles = 0;

		while (countOfCycles < count) {
			Calendar yesterday = (Calendar) firstDayOfCycle.clone();
			yesterday.add(Calendar.DAY_OF_MONTH, -1);

			Calendar firstDayOfPrevCycle = getFirstDayOfCycle(yesterday);
			if (firstDayOfPrevCycle == null) {
				break;
			}

			int difference = DateUtils.getDifferenceInDays(firstDayOfCycle,
					firstDayOfPrevCycle);
			if (difference <= 60) {
				sum += difference;
				++actualCount;
			}

			++countOfCycles;
		}

		return actualCount == 0 ? 28 : (int) Math.round(sum / actualCount);
	}

	public int getLenOfCurrentMenstrualCycle(Calendar current) {
		Calendar firstDayOfCycle = getFirstDayOfCycle(current);
		if (firstDayOfCycle == null) {
			return 0;
		}

		int currentIndex = getLeftNeighborIndex(current);
		CalendarData currentData = mDataKeeper.get(currentIndex);
		Calendar firstDayOfNextCycle;

		do {
			++currentIndex;
			currentData = mDataKeeper.get(currentIndex);
			if (currentData != null) {
				firstDayOfNextCycle = getFirstDayOfCycle(currentData.getDate());
			} else {
				firstDayOfNextCycle = null;
			}
		} while (firstDayOfNextCycle != null
				&& DateUtils
						.datesAreEqual(firstDayOfCycle, firstDayOfNextCycle));

		if (firstDayOfNextCycle == null) {
			int avgLen = getAvgLenOfLastMenstrualCycles(firstDayOfCycle);
			return avgLen;
		} else {
			int difference = DateUtils.getDifferenceInDays(firstDayOfNextCycle,
					firstDayOfCycle);
			return difference;
		}
	}
}
