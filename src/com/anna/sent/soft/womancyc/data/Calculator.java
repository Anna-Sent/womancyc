package com.anna.sent.soft.womancyc.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

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
		int dayOfCycle;
		Calendar firstDayOfCycle = getFirstDayOfCycle(current);
		if (firstDayOfCycle == null) {
			dayOfCycle = 0;
		} else {
			int currentIndex = getLeftNeighborIndex(current);
			CalendarData currentData = mDataKeeper.get(currentIndex);
			Calendar firstDayOfNextCycle;

			do {
				++currentIndex;
				currentData = mDataKeeper.get(currentIndex);
				if (currentData != null) {
					firstDayOfNextCycle = getFirstDayOfCycle(currentData
							.getDate());
				} else {
					firstDayOfNextCycle = null;
				}
			} while (firstDayOfNextCycle != null
					&& DateUtils.datesAreEqual(firstDayOfCycle,
							firstDayOfNextCycle));

			Calendar today = Calendar.getInstance();
			if (firstDayOfNextCycle == null && DateUtils.after(current, today)) {
				int avgLen = getAvgLenOfLastMenstrualCycles(firstDayOfCycle);

				Calendar expectedFirstDayOfCycle;
				if (DateUtils.getDifferenceInDays(today, firstDayOfCycle) + 1 > avgLen) {
					expectedFirstDayOfCycle = (Calendar) today.clone();
					expectedFirstDayOfCycle.add(Calendar.DAY_OF_MONTH, 1);
				} else {
					expectedFirstDayOfCycle = firstDayOfCycle;
				}

				dayOfCycle = DateUtils.getDifferenceInDays(current,
						expectedFirstDayOfCycle) % avgLen + 1;
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
			firstDayOfCycle = firstDayOfPrevCycle;
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

	public class Value {
		public final int avg, min, max;

		public Value(int avg, int min, int max) {
			this.avg = avg;
			this.min = min;
			this.max = max;
		}
	}

	public class Row {
		public final Calendar firstDayOfCycle;
		public final int menstrualCycleLen;
		public final int bleedingLen;

		public Row(Calendar firstDayOfCycle, int menstrualCycleLen,
				int bleedingLen) {
			this.firstDayOfCycle = firstDayOfCycle;
			this.menstrualCycleLen = menstrualCycleLen;
			this.bleedingLen = bleedingLen;
		}
	}

	public class Statistic {
		public final Value MCL;
		public final Value BL;
		public final List<Row> rows;

		public Statistic(Value mcl, Value bl, List<Row> rows) {
			MCL = mcl;
			BL = bl;
			this.rows = rows;
		}
	}

	public Statistic getStatistic() {
		int lastIndex = mDataKeeper.getCount() - 1;
		Calendar firstDayOfLastCycle = getFirstDayOfCycle(mDataKeeper.get(
				lastIndex).getDate());

		if (firstDayOfLastCycle == null) {
			Value mcl = new Value(0, 0, 0);
			Value bl = new Value(0, 0, 0);
			return new Statistic(mcl, bl, new ArrayList<Row>());
		} else {
			double mcSum = 0;
			int mcActualCount = 0;
			int mcMax = 0;
			int mcMin = 0;
			double bSum = 0;
			int bActualCount = 0;
			int bMax = 0;
			int bMin = 0;
			List<Row> rows = new ArrayList<Row>();
			Calendar firstDayOfCycle = (Calendar) firstDayOfLastCycle.clone();
			Calendar yesterday = (Calendar) firstDayOfCycle.clone();
			yesterday.add(Calendar.DAY_OF_MONTH, -1);
			Calendar firstDayOfPrevCycle = getFirstDayOfCycle(yesterday);

			while (firstDayOfPrevCycle != null) {
				int bleedingLen = 0;
				Calendar current = (Calendar) firstDayOfPrevCycle.clone();
				CalendarData dayOfBleedingData = mDataKeeper.get(current);
				while (dayOfBleedingData != null
						&& dayOfBleedingData.getMenstruation() != 0) {
					++bleedingLen;
					current.add(Calendar.DAY_OF_MONTH, 1);
					dayOfBleedingData = mDataKeeper.get(current);
				}

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

				int cycleLen = DateUtils.getDifferenceInDays(firstDayOfCycle,
						firstDayOfPrevCycle);

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

				rows.add(new Row(firstDayOfPrevCycle, cycleLen, bleedingLen));
				firstDayOfCycle = firstDayOfPrevCycle;
				Calendar yesterday_i = (Calendar) firstDayOfPrevCycle.clone();
				yesterday_i.add(Calendar.DAY_OF_MONTH, -1);
				firstDayOfPrevCycle = getFirstDayOfCycle(yesterday_i);
			}

			int mcAvg = mcActualCount == 0 ? 0 : (int) Math.round(mcSum
					/ mcActualCount);
			int bAvg = bActualCount == 0 ? 0 : (int) Math.round(bSum
					/ bActualCount);
			Value mcl = new Value(mcAvg, mcMin, mcMax);
			Value bl = new Value(bAvg, bMin, bMax);
			return new Statistic(mcl, bl, rows);
		}
	}
}
