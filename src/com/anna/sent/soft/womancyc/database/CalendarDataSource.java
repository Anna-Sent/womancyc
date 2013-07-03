package com.anna.sent.soft.womancyc.database;

import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anna.sent.soft.womancyc.data.CalendarData;

public class CalendarDataSource {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private SQLiteDatabase mDatabase = null;
	private CalendarHelper mHelper;

	public CalendarDataSource(Context context) {
		mHelper = new CalendarHelper(context);
	}

	public void open() throws SQLException {
		mDatabase = mHelper.getWritableDatabase();
	}

	private boolean isOpen() {
		return mDatabase != null;
	}

	public void clearAllData() {
		if (isOpen()) {
			mHelper.recreateDatabase(mDatabase);
		}
	}

	public void close() {
		if (isOpen()) {
			mHelper.close();
			mDatabase = null;
		}
	}

	public boolean insert(CalendarData value) {
		if (isOpen()) {
			ContentValues values = new ContentValues();
			values.put(CalendarHelper.COLUMN_ID, value.getId());
			values.put(CalendarHelper.COLUMN_MENSTRUATION,
					value.getMenstruation());
			values.put(CalendarHelper.COLUMN_SEX, value.getSex());
			values.put(CalendarHelper.COLUMN_TOOK_PILL, value.getTookPill());
			values.put(CalendarHelper.COLUMN_NOTE, value.getNote());
			long id = mDatabase.insert(CalendarHelper.TABLE_CALENDAR, null,
					values);
			log("Insert calendar: " + value.toString());
			return id != -1;
		}

		return false;
	}

	public boolean delete(CalendarData value) {
		if (isOpen()) {
			int rows = mDatabase.delete(CalendarHelper.TABLE_CALENDAR,
					CalendarHelper.COLUMN_ID + " = " + value.getId(), null);
			log("Delete calendar: " + value.toString());
			return rows > 0;
		}

		return false;
	}

	public boolean update(CalendarData value) {
		if (isOpen()) {
			ContentValues values = new ContentValues();
			values.put(CalendarHelper.COLUMN_MENSTRUATION,
					value.getMenstruation());
			values.put(CalendarHelper.COLUMN_SEX, value.getSex());
			values.put(CalendarHelper.COLUMN_TOOK_PILL, value.getTookPill());
			values.put(CalendarHelper.COLUMN_NOTE, value.getNote());
			int rows = mDatabase.update(CalendarHelper.TABLE_CALENDAR, values,
					CalendarHelper.COLUMN_ID + " = " + value.getId(), null);
			log("Update calendar: " + value.toString());
			return rows > 0;
		}

		return false;
	}

	public void getAllRows(List<CalendarData> list) {
		list.clear();
		if (isOpen()) {
			Cursor cursor = mDatabase.query(CalendarHelper.TABLE_CALENDAR,
					CalendarHelper.AllColumns, null, null, null, null,
					CalendarHelper.COLUMN_ID);
			cursor.moveToFirst();
			log("Load calendar data:");
			while (!cursor.isAfterLast()) {
				CalendarData row = cursorToCalendar(cursor);
				list.add(row);
				cursor.moveToNext();
				log(row.toString());
			}

			cursor.close();
		}
	}

	public void getAllNotes(List<String> list) {
		list.clear();
		if (isOpen()) {
			Cursor cursor = mDatabase.query(true,
					CalendarHelper.TABLE_CALENDAR,
					new String[] { CalendarHelper.COLUMN_NOTE }, null, null,
					null, null, CalendarHelper.COLUMN_NOTE, null);
			cursor.moveToFirst();
			log("Load notes:");
			while (!cursor.isAfterLast()) {
				String row = cursorToNote(cursor);
				list.add(row);
				cursor.moveToNext();
				log(row);
			}

			cursor.close();
		}
	}

	private CalendarData cursorToCalendar(Cursor cursor) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(cursor.getLong(CalendarHelper.COLUMN_INDEX_ID));
		CalendarData calendar = new CalendarData(date);
		calendar.setMenstruation(cursor
				.getInt(CalendarHelper.COLUMN_INDEX_MENSTRUATION));
		calendar.setSex(cursor.getInt(CalendarHelper.COLUMN_INDEX_SEX));
		calendar.setTookPill(cursor
				.getInt(CalendarHelper.COLUMN_INDEX_TOOK_PILL) != 0);
		calendar.setNote(cursor.getString(CalendarHelper.COLUMN_INDEX_NOTE));
		return calendar;
	}

	private String cursorToNote(Cursor cursor) {
		String result = cursor.getString(0);
		return result;
	}
}