package com.anna.sent.soft.womancyc.database;

import java.util.ArrayList;
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
	private static final boolean DEBUG = true;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private SQLiteDatabase mDatabase;
	private CalendarHelper mHelper;
	private String[] mAllColumns = { CalendarHelper.COLUMN_ID,
			CalendarHelper.COLUMN_MENSTRUATION, CalendarHelper.COLUMN_SEX,
			CalendarHelper.COLUMN_NOTE };

	public CalendarDataSource(Context context) {
		mHelper = new CalendarHelper(context);
	}

	public void open() throws SQLException {
		mDatabase = mHelper.getWritableDatabase();
	}

	public void close() {
		mHelper.close();
	}

	public void insert(CalendarData value) {
		ContentValues values = new ContentValues();
		values.put(CalendarHelper.COLUMN_ID, value.getId());
		values.put(CalendarHelper.COLUMN_MENSTRUATION, value.getMenstruation());
		values.put(CalendarHelper.COLUMN_SEX, value.getSex());
		values.put(CalendarHelper.COLUMN_NOTE, value.getNote());
		mDatabase.insert(CalendarHelper.TABLE_CALENDAR, null, values);
		log("Insert calendar: " + value.toString());
	}

	public void delete(CalendarData value) {
		mDatabase.delete(CalendarHelper.TABLE_CALENDAR,
				CalendarHelper.COLUMN_ID + " = " + value.getId(), null);
		log("Delete calendar: " + value.toString());
	}

	public void update(CalendarData value) {
		ContentValues values = new ContentValues();
		values.put(CalendarHelper.COLUMN_MENSTRUATION, value.getMenstruation());
		values.put(CalendarHelper.COLUMN_SEX, value.getSex());
		values.put(CalendarHelper.COLUMN_NOTE, value.getNote());
		mDatabase.update(CalendarHelper.TABLE_CALENDAR, values,
				CalendarHelper.COLUMN_ID + " = " + value.getId(), null);
		log("Update calendar: " + value.toString());
	}

	public List<CalendarData> getAll() {
		List<CalendarData> list = new ArrayList<CalendarData>();
		Cursor cursor = mDatabase.query(CalendarHelper.TABLE_CALENDAR,
				mAllColumns, null, null, null, null, CalendarHelper.COLUMN_ID);
		cursor.moveToFirst();
		log("Load data:");
		while (!cursor.isAfterLast()) {
			CalendarData row = cursorToCalendar(cursor);
			list.add(row);
			cursor.moveToNext();
			log(row.toString());
		}

		cursor.close();
		return list;
	}

	private CalendarData cursorToCalendar(Cursor cursor) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(cursor.getLong(CalendarHelper.COLUMN_INDEX_ID));
		CalendarData calendar = new CalendarData(date);
		calendar.setMenstruation(cursor
				.getInt(CalendarHelper.COLUMN_INDEX_MENSTRUATION));
		calendar.setSex(cursor.getInt(CalendarHelper.COLUMN_INDEX_SEX));
		calendar.setNote(cursor.getString(CalendarHelper.COLUMN_INDEX_NOTE));
		return calendar;
	}
}