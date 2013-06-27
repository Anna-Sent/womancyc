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

	public void openDataSource() throws SQLException {
		mDataSource.open();
		mDataSource.getAllRows(mValues);
		mDataSource.getAllNotes(mNotes);
	}

	public void closeDataSource() {
		mDataSource.close();
	}

	public void clearAllData() {
		mDataSource.clearAllData();
		mDataSource.getAllRows(mValues);
		mDataSource.getAllNotes(mNotes);
	}

	@Override
	public CalendarData get(Calendar date) {
		int index = indexOf(date);
		if (index >= 0) {
			return mValues.get(index);
		} else {
			return null;
		}
	}

	@Override
	public int indexOf(Calendar date) {
		return Collections.binarySearch(mValues, new CalendarData(date),
				new CalendarDataComparator());
	}

	@Override
	public CalendarData get(int index) {
		if (index >= 0 && index < mValues.size()) {
			return mValues.get(index);
		} else {
			return null;
		}
	}

	@Override
	public int getCount() {
		return mValues.size();
	}

	@Override
	public void insertOrUpdate(CalendarData value) {
		int index = indexOf(value);
		if (index >= 0) {
			mDataSource.update(value);
			mValues.set(index, value);
		} else {
			mDataSource.insert(value);
			mValues.add(-index - 1, value);
		}

		mDataSource.getAllNotes(mNotes);
	}

	@Override
	public void delete(CalendarData value) {
		int index = indexOf(value);
		if (index >= 0) {
			mDataSource.delete(value);
			mValues.remove(index);

			mDataSource.getAllNotes(mNotes);
		}
	}

	@Override
	public List<String> getNotes() {
		return mNotes;
	}

	private int indexOf(CalendarData value) {
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