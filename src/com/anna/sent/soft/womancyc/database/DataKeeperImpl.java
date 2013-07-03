package com.anna.sent.soft.womancyc.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.database.SQLException;

import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public class DataKeeperImpl implements DataKeeper {
	private Context mContext;
	private CalendarDataSource mDataSource;
	private List<CalendarData> mValues = new ArrayList<CalendarData>();
	private List<String> mNotes = new ArrayList<String>();

	public DataKeeperImpl(Context context) {
		mContext = context;
		mDataSource = new CalendarDataSource(mContext);
	}

	public synchronized void openDataSource() throws SQLException {
		mDataSource.open();
		mDataSource.getAllRows(mValues);
		mDataSource.getAllNotes(mNotes);
	}

	public synchronized void closeDataSource() {
		mDataSource.close();
	}

	public synchronized void clearAllData() {
		mDataSource.clearAllData();
		mDataSource.getAllRows(mValues);
		mDataSource.getAllNotes(mNotes);
	}

	@Override
	public synchronized CalendarData get(Calendar date) {
		int index = indexOf(date);
		if (index >= 0) {
			return mValues.get(index);
		} else {
			return null;
		}
	}

	@Override
	public synchronized int indexOf(Calendar date) {
		return Collections.binarySearch(mValues, new CalendarData(date),
				new CalendarDataComparator());
	}

	@Override
	public synchronized CalendarData get(int index) {
		if (index >= 0 && index < mValues.size()) {
			return mValues.get(index);
		} else {
			return null;
		}
	}

	@Override
	public synchronized int getCount() {
		return mValues.size();
	}

	@Override
	public synchronized void insertOrUpdate(CalendarData value) {
		if (value.isEmpty()) {
			delete(value);
			return;
		}

		int index = indexOf(value);
		if (index >= 0) {
			boolean updated = mDataSource.update(value);
			if (updated) {
				mValues.set(index, value);
				mDataSource.getAllNotes(mNotes);
			}
		} else {
			boolean inserted = mDataSource.insert(value);
			if (inserted) {
				mValues.add(-index - 1, value);
				mDataSource.getAllNotes(mNotes);
			}
		}
	}

	@Override
	public synchronized void delete(CalendarData value) {
		int index = indexOf(value);
		if (index >= 0) {
			boolean deleted = mDataSource.delete(value);
			if (deleted) {
				mValues.remove(index);
				mDataSource.getAllNotes(mNotes);
			}
		}
	}

	@Override
	public synchronized List<String> getNotes() {
		return mNotes;
	}

	private synchronized int indexOf(CalendarData value) {
		return Collections.binarySearch(mValues, value,
				new CalendarDataComparator());
	}

	private class CalendarDataComparator implements Comparator<CalendarData> {
		@Override
		public int compare(CalendarData lhs, CalendarData rhs) {
			Calendar date1 = lhs.getDate();
			Calendar date2 = rhs.getDate();
			if (DateUtils.datesAreEqual(date1, date2)) {
				return 0;
			} else {
				return date1.after(date2) ? 1 : -1;
			}
		}
	}
}