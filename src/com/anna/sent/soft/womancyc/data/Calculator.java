package com.anna.sent.soft.womancyc.data;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import android.content.Context;
import android.util.Log;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.shared.Settings;

public class Calculator {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private static String wrapMsg(String msg) {
		return Calculator.class.getSimpleName() + ": " + msg;
	}

	@SuppressWarnings("unused")
	private static void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private final int MAX_MENSTRUAL_CYCLE_LEN;
	@SuppressWarnings("unused")
	private final int MIN_MENSTRUAL_CYCLE_LEN;

	/**
	 * Must be not null.
	 */
	private DataKeeper mDataKeeper;
	private int defaultMenstrualCycleLen;
	private boolean useAvg;

	public Calculator(Context context, DataKeeper dataKeeper) {
		mDataKeeper = dataKeeper;
		defaultMenstrualCycleLen = Settings
				.getDefaultMenstrualCycleLen(context);
		useAvg = Settings.useAverage(context);
		MAX_MENSTRUAL_CYCLE_LEN = getMaxMenstrualCycleLen(context);
		MIN_MENSTRUAL_CYCLE_LEN = getMinMenstrualCycleLen(context);
	}

	public static int getMaxMenstrualCycleLen(Context context) {
		return context.getResources()
				.getInteger(R.integer.maxMenstrualCycleLen);
	}

	public static int getMinMenstrualCycleLen(Context context) {
		return context.getResources()
				.getInteger(R.integer.minMenstrualCycleLen);
	}

	public int getDayOfCycle(LocalDate current) {
		int dayOfCycle;
		LocalDate firstDayOfCycle = getFirstDayOfCycle(current);
		if (firstDayOfCycle == null) {
			dayOfCycle = 0;
		} else {
			LocalDate firstDayOfNextCycle = getFirstDayOfNextCycle(current);
			LocalDate today = LocalDate.now();
			if (firstDayOfNextCycle == null && current.isAfter(today)) {
				int avgLen = getAvgLenOfLastMenstrualCycles(firstDayOfCycle);

				LocalDate expectedFirstDayOfCycle;
				if (getDifference(firstDayOfCycle, today) + 1 > avgLen) {
					expectedFirstDayOfCycle = today.plusDays(1);
				} else {
					expectedFirstDayOfCycle = firstDayOfCycle;
				}

				dayOfCycle = getDifference(expectedFirstDayOfCycle, current)
						% avgLen + 1;
			} else {
				dayOfCycle = getDifference(firstDayOfCycle, current) + 1;
			}
		}

		return dayOfCycle;
	}

	private int getDifference(LocalDate start, LocalDate end) {
		return Days.daysBetween(start.toDateTimeAtStartOfDay(),
				end.toDateTimeAtStartOfDay()).getDays();
	}

	private int getLeftNeighborIndex(LocalDate current) {
		int currentIndex = mDataKeeper.indexOf(current);
		if (currentIndex >= 0) {
			return currentIndex;
		} else {
			return -currentIndex - 2;
		}
	}

	public LocalDate getFirstDayOfCycle(LocalDate current) {
		int currentIndex = getLeftNeighborIndex(current);
		CalendarData currentData = mDataKeeper.get(currentIndex);

		while (currentData != null && currentData.getMenstruation() == 0) {
			--currentIndex;
			currentData = mDataKeeper.get(currentIndex);
		}

		if (currentData == null) {
			return null;
		} else {
			CalendarData firstDayOfCycleData;
			LocalDate yesterday = currentData.getDate();
			do {
				firstDayOfCycleData = currentData;
				yesterday = yesterday.minusDays(1);
				currentData = mDataKeeper.get(yesterday);
			} while (currentData != null && currentData.getMenstruation() != 0);

			return firstDayOfCycleData.getDate();
		}
	}

	private LocalDate getFirstDayOfNextCycle(LocalDate current) {
		int currentIndex = getLeftNeighborIndex(current);
		CalendarData currentData = mDataKeeper.get(currentIndex);

		if (currentData == null) {
			currentIndex = 0;
		} else {
			LocalDate tomorrow = currentData.getDate();

			while (currentData != null && currentData.getMenstruation() != 0) {
				tomorrow = tomorrow.plusDays(1);
				currentIndex = mDataKeeper.indexOf(tomorrow);
				currentData = mDataKeeper.get(currentIndex);
			}

			if (currentIndex < 0) {
				currentIndex = -currentIndex - 1;
			}
		}

		currentData = mDataKeeper.get(currentIndex);
		while (currentData != null && currentData.getMenstruation() == 0) {
			++currentIndex;
			currentData = mDataKeeper.get(currentIndex);
		}

		if (currentData == null) {
			return null;
		} else {
			return currentData.getDate();
		}
	}

	private LocalDate getFirstDayOfPrevCycle(LocalDate current) {
		LocalDate firstDayOfCycle = getFirstDayOfCycle(current);
		if (firstDayOfCycle == null) {
			return null;
		} else {
			LocalDate yesterday = firstDayOfCycle.minusDays(1);
			LocalDate firstDayOfPrevCycle = getFirstDayOfCycle(yesterday);
			return firstDayOfPrevCycle;
		}
	}

	private int getAvgLenOfLastMenstrualCycles(LocalDate firstDayOfCycle) {
		if (useAvg) {
			int sum = 0;
			final int count = 3;
			int actualCount = 0;
			int countOfCycles = 0;
			LocalDate firstDayOfPrevCycle = getFirstDayOfPrevCycle(firstDayOfCycle);

			while (countOfCycles < count && firstDayOfPrevCycle != null) {
				int difference = getDifference(firstDayOfPrevCycle,
						firstDayOfCycle);
				if (difference <= MAX_MENSTRUAL_CYCLE_LEN && difference > 0) {
					sum += difference;
					++actualCount;
				}

				++countOfCycles;
				firstDayOfCycle = firstDayOfPrevCycle;
				firstDayOfPrevCycle = getFirstDayOfPrevCycle(firstDayOfCycle);
			}

			return actualCount == 0 || sum <= 0 ? defaultMenstrualCycleLen
					: (int) Math.round((double) sum / actualCount);
		} else {
			return defaultMenstrualCycleLen;
		}
	}

	public int getLenOfCurrentMenstrualCycle(LocalDate current) {
		LocalDate firstDayOfCycle = getFirstDayOfCycle(current);
		if (firstDayOfCycle == null) {
			return 0;
		}

		LocalDate firstDayOfNextCycle = getFirstDayOfNextCycle(current);
		if (firstDayOfNextCycle == null) {
			int avgLen = getAvgLenOfLastMenstrualCycles(firstDayOfCycle);
			return avgLen;
		} else {
			int difference = getDifference(firstDayOfCycle, firstDayOfNextCycle);
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
		public final LocalDate firstDayOfCycle;
		public final int menstrualCycleLen;
		public final int bleedingLen;

		public Row(LocalDate firstDayOfCycle, int menstrualCycleLen,
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
		CalendarData lastData = mDataKeeper.get(lastIndex);
		LocalDate firstDayOfLastCycle = lastData == null ? null
				: getFirstDayOfCycle(lastData.getDate());

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
			LocalDate firstDayOfCycle = firstDayOfLastCycle;
			LocalDate firstDayOfPrevCycle = getFirstDayOfPrevCycle(firstDayOfCycle);

			while (firstDayOfPrevCycle != null) {
				int bleedingLen = 0;
				LocalDate current = firstDayOfPrevCycle;
				CalendarData dayOfBleedingData = mDataKeeper.get(current);
				while (dayOfBleedingData != null
						&& dayOfBleedingData.getMenstruation() != 0) {
					++bleedingLen;
					current = current.plusDays(1);
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

				int cycleLen = getDifference(firstDayOfPrevCycle,
						firstDayOfCycle);

				if (cycleLen <= MAX_MENSTRUAL_CYCLE_LEN) {
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
				firstDayOfPrevCycle = getFirstDayOfPrevCycle(firstDayOfCycle);
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