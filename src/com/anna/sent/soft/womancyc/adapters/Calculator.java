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
			return (Calendar) firstDayOfCycleData.getDate().clone();
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

	public class MenstrualCycleLenght {
		public int avg, min, max;

		public MenstrualCycleLenght(int avg, int min, int max) {
			this.avg = avg;
			this.min = min;
			this.max = max;
		}
	}

	public class BleedingLenght {
		public int avg, min, max;

		public BleedingLenght(int avg, int min, int max) {
			this.avg = avg;
			this.min = min;
			this.max = max;
		}
	}

	public class Statistic {
		public MenstrualCycleLenght MCL;
		public BleedingLenght BL;

		public Statistic(MenstrualCycleLenght mcl, BleedingLenght bl) {
			MCL = mcl;
			BL = bl;
		}
	}

	public Statistic getStatistic() {
		int currentIndex = mDataKeeper.getCount() - 1;
		CalendarData firstDayOfLastCycleData = mDataKeeper.get(currentIndex);
		while (firstDayOfLastCycleData != null
				&& firstDayOfLastCycleData.getMenstruation() == 0) {
			--currentIndex;
			firstDayOfLastCycleData = mDataKeeper.get(currentIndex);
		}

		if (firstDayOfLastCycleData == null) {
			MenstrualCycleLenght mcl = new MenstrualCycleLenght(0, 0, 0);
			BleedingLenght bl = new BleedingLenght(0, 0, 0);
			return new Statistic(mcl, bl);
		} else {
			Calendar firstDayOfCycle = (Calendar) firstDayOfLastCycleData
					.getDate().clone();
			double mcSum = 0;
			int mcActualCount = 0;
			int mcMax = 0;
			int mcMin = 0;
			double bSum = 0;
			int bActualCount = 0;
			int bMax = 0;
			int bMin = 0;

			do {
				Calendar yesterday = (Calendar) firstDayOfCycle.clone();
				yesterday.add(Calendar.DAY_OF_MONTH, -1);
				Calendar firstDayOfPrevCycle = getFirstDayOfCycle(yesterday);

				int bleedingLen = 0;
				@SuppressWarnings("unused")
				Calendar firstDayOfBleeding = (Calendar) firstDayOfCycle
						.clone();
				Calendar lastDayOfBleeding = (Calendar) firstDayOfCycle.clone();
				Calendar current = (Calendar) firstDayOfCycle.clone();
				CalendarData dayOfBleedingData = mDataKeeper.get(current);
				while (dayOfBleedingData != null
						&& dayOfBleedingData.getMenstruation() != 0) {
					++bleedingLen;
					lastDayOfBleeding = (Calendar) current.clone();
					current.add(Calendar.DAY_OF_MONTH, 1);
					dayOfBleedingData = mDataKeeper.get(current);
				}

				Calendar today = Calendar.getInstance();
				if (firstDayOfPrevCycle != null) {
					int cycleLen = DateUtils.getDifferenceInDays(
							firstDayOfCycle, firstDayOfPrevCycle);

					if (DateUtils.beforeOrEqual(lastDayOfBleeding, today)) {
						if (cycleLen <= 60) {
							mcSum += cycleLen;
							++mcActualCount;

							if (cycleLen > mcMax) {
								mcMax = cycleLen;
							}

							if (mcMin == 0) {
								mcMin = cycleLen;
							}

							if (cycleLen < mcMin) {
								mcMin = cycleLen;
							}
						}
					}
				}

				if (DateUtils.before(lastDayOfBleeding, today)) {
					bSum += bleedingLen;
					++bActualCount;

					if (bleedingLen > bMax) {
						bMax = bleedingLen;
					}

					if (bMin == 0) {
						bMin = bleedingLen;
					}

					if (bleedingLen < bMin) {
						bMin = bleedingLen;
					}
				}

				firstDayOfCycle = firstDayOfPrevCycle;
			} while (firstDayOfCycle != null);

			int mcAvg = mcActualCount == 0 ? 0 : (int) Math.round(mcSum
					/ mcActualCount);
			int bAvg = bActualCount == 0 ? 0 : (int) Math.round(bSum
					/ bActualCount);
			MenstrualCycleLenght mcl = new MenstrualCycleLenght(mcAvg, mcMin,
					mcMax);
			BleedingLenght bl = new BleedingLenght(bAvg, bMin, bMax);
			return new Statistic(mcl, bl);
		}
	}
}
