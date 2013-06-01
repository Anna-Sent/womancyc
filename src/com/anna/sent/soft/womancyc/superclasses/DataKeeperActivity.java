package com.anna.sent.soft.womancyc.superclasses;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.database.CalendarDataSource;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public abstract class DataKeeperActivity extends StateSaverActivity implements
		DataKeeper {
	private static final String TAG = "moo";
	private static final boolean DEBUG = true;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	@SuppressWarnings("unused")
	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private CalendarDataSource mDataSource;
	private List<CalendarData> mValues;
	private List<String> mNotes;

	@Override
	public void setViews(Bundle savedInstanceState) {
		super.setViews(savedInstanceState);
		mDataSource = new CalendarDataSource(this);
		mDataSource.open();
		mValues = mDataSource.getAllRows();
		mNotes = mDataSource.getAllNotes();
		dataChanged();
	}

	@Override
	protected void onResume() {
		try {
			mDataSource.open();
			mValues = mDataSource.getAllRows();
			mNotes = mDataSource.getAllNotes();
			dataChanged();
		} catch (SQLException e) {
			e.printStackTrace();
			Toast.makeText(this,
					getString(R.string.errorWhileOpenningDatabase),
					Toast.LENGTH_LONG).show();
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mDataSource.close();
	}

	@Override
	public List<String> getNotes() {
		return mNotes;
	}

	protected abstract void dataChanged();

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

		mNotes = mDataSource.getAllNotes();
		dataChanged();
	}

	@Override
	public void delete(CalendarData value) {
		int index = indexOf(value);
		if (index >= 0) {
			mDataSource.delete(value);
			mValues.remove(index);

			mNotes = mDataSource.getAllNotes();
			dataChanged();
		}
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

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof DataKeeperClient) {
			((DataKeeperClient) fragment).setDataKeeper(this);
		}
	}
}
