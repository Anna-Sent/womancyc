package com.anna.sent.soft.womancyc.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public class DataKeeperImpl implements DataKeeper {
	private static final String TAG = "moo";
	private static final boolean DEBUG_SYNC = true;
	private static final boolean DEBUG_BIN = false;
	private static final boolean DEBUG_CRUD = false;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	private void log(String msg, boolean scenario) {
		if (scenario) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

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
		log("search " + DateUtils.toString(date), DEBUG_BIN);
		int low = 0;
		int high = mValues.size() - 1;
		log("low " + low + " high " + high, DEBUG_BIN);

		while (low <= high) {
			log("low " + low + " high " + high, DEBUG_BIN);
			int mid = (low + high) >>> 1;
			log("mid " + mid, DEBUG_BIN);
			Calendar midVal = mValues.get(mid).getDate();
			int compare = DateUtils.compare(midVal, date);
			log("miv val " + DateUtils.toString(midVal), DEBUG_BIN);
			log("compare is " + compare, DEBUG_BIN);

			if (compare == -1) {
				low = mid + 1;
			} else if (compare == 1) {
				high = mid - 1;
			} else {
				return mid; // key found
			}
		}

		return -(low + 1); // key not found
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
			log("value is empty", DEBUG_CRUD);
			delete(value);
			return;
		}

		int index = indexOf(value);
		log("index of value is " + index, DEBUG_CRUD);
		if (index >= 0) {
			boolean updated = mDataSource.update(value);
			if (updated) {
				log("updated", DEBUG_CRUD);
				mValues.set(index, value);
				checkSync();
				mDataSource.getAllNotes(mNotes);
			}
		} else {
			boolean inserted = mDataSource.insert(value);
			if (inserted) {
				log("inserted", DEBUG_CRUD);
				mValues.add(-index - 1, value);
				checkSync();
				mDataSource.getAllNotes(mNotes);
			}
		}
	}

	private void checkSync() {
		if (DEBUG_SYNC) {
			List<CalendarData> dbValues = new ArrayList<CalendarData>();
			mDataSource.getAllRows(dbValues);
			int count = mValues.size();
			int dbCount = dbValues.size();
			if (count != dbCount) {
				log("counts differ: " + count + " in memory, " + dbCount
						+ " in db", DEBUG_SYNC);
				printValues(mValues);
				printValues(dbValues);
			} else {
				boolean synced = true;
				for (int i = 0; i < count && synced; ++i) {
					CalendarData value = mValues.get(i);
					CalendarData dbValue = dbValues.get(i);
					if (!value.equals(dbValue)) {
						synced = false;
					}
				}

				if (synced) {
					log("synced!", DEBUG_SYNC);
				} else {
					log("in memory", DEBUG_SYNC);
					printValues(mValues);
					log("in db", DEBUG_SYNC);
					printValues(dbValues);
					log("elements differ!", DEBUG_SYNC);
				}
			}
		}
	}

	private void printValues(List<CalendarData> values) {
		if (DEBUG_SYNC) {
			String result = "";
			int size = values.size() - 1;
			for (int i = 0; i <= size; ++i) {
				result += values.get(i).toString() + (i == size ? "" : "; ");
			}

			log(result, DEBUG_SYNC);
		}
	}

	@Override
	public synchronized void delete(CalendarData value) {
		int index = indexOf(value);
		log("index of value is " + index, DEBUG_CRUD);
		if (index >= 0) {
			boolean deleted = mDataSource.delete(value);
			if (deleted) {
				log("deleted", DEBUG_CRUD);
				mValues.remove(index);
				checkSync();
				mDataSource.getAllNotes(mNotes);
			}
		}
	}

	@Override
	public synchronized List<String> getNotes() {
		return mNotes;
	}

	private int indexOf(CalendarData value) {
		Calendar date = value.getDate();
		return indexOf(date);
	}
}