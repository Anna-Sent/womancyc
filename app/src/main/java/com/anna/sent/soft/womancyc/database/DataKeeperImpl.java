package com.anna.sent.soft.womancyc.database;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.anna.sent.soft.logging.MyLog;
import com.anna.sent.soft.womancyc.BuildConfig;
import com.anna.sent.soft.womancyc.data.CalendarData;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class DataKeeperImpl implements DataKeeper {
    private static final boolean DEBUG_SYNC = false;

    private final CalendarDataSource mDataSource;
    private final List<CalendarData> mValues = new ArrayList<>();
    private final List<String> mNotes = new ArrayList<>();

    public DataKeeperImpl(Context context) {
        mDataSource = new CalendarDataSource(context);
    }

    private String wrapMsg(String msg) {
        return getClass().getSimpleName() + ": " + msg;
    }

    private void log(String msg) {
        MyLog.getInstance().logcat(Log.DEBUG, wrapMsg(msg));
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
    public synchronized CalendarData get(LocalDate date) {
        int index = indexOf(date);
        if (index >= 0) {
            return mValues.get(index);
        } else {
            return null;
        }
    }

    @Override
    public synchronized int indexOf(LocalDate date) {
        int low = 0;
        int high = mValues.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            LocalDate midVal = mValues.get(mid).getDate();
            int compare = midVal.compareTo(date);

            if (compare < 0) {
                low = mid + 1;
            } else if (compare > 0) {
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

        checkSync();
    }

    @SuppressWarnings("UnusedAssignment")
    private void checkSync() {
        if (DEBUG_SYNC && BuildConfig.DEBUG) {
            List<CalendarData> dbValues = new ArrayList<>();
            mDataSource.getAllRows(dbValues);
            int count = mValues.size();
            int dbCount = dbValues.size();
            if (count != dbCount) {
                log("counts differ: " + count + " in memory, " + dbCount + " in db");
                printValues(mValues);
                printValues(dbValues);
            } else {
                boolean synced = true;
                for (int i = 0; i < count; ++i) {
                    CalendarData value = mValues.get(i);
                    CalendarData dbValue = dbValues.get(i);
                    if (!value.equals(dbValue)) {
                        synced = false;
                        log("in db: " + dbValue + "; in memory: " + value);
                    }
                }

                if (synced) {
                    log("synced!");
                } else {
                    log("elements differ!");
                }
            }
        }
    }

    private void printValues(List<CalendarData> values) {
        if (BuildConfig.DEBUG) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < values.size(); ++i) {
                result.append(values.get(i)).append(i == values.size() - 1 ? "" : "; ");
            }

            log(result.toString());
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

        checkSync();
    }

    @Override
    public synchronized List<String> getNotes() {
        return mNotes;
    }

    private int indexOf(CalendarData value) {
        return indexOf(value.getDate());
    }
}
