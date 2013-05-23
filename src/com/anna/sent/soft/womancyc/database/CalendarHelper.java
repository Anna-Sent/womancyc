package com.anna.sent.soft.womancyc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CalendarHelper extends SQLiteOpenHelper {
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

	public static final String TABLE_CALENDAR = "calendar";
	public static final String COLUMN_ID = "_id";
	public static final int COLUMN_INDEX_ID = 0;
	public static final String COLUMN_MENSTRUATION = "menstruation";
	public static final int COLUMN_INDEX_MENSTRUATION = 1;
	public static final String COLUMN_SEX = "sex";
	public static final int COLUMN_INDEX_SEX = 2;
	public static final String COLUMN_NOTE = "note";
	public static final int COLUMN_INDEX_NOTE = 3;

	public static final String[] AllColumns = { CalendarHelper.COLUMN_ID,
			CalendarHelper.COLUMN_MENSTRUATION, CalendarHelper.COLUMN_SEX,
			CalendarHelper.COLUMN_NOTE };

	private static final String DATABASE_NAME = "WomanCycCalendar.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_CALENDAR + "(" + COLUMN_ID + " integer primary key, "
			+ COLUMN_MENSTRUATION + " integer null, " + COLUMN_SEX
			+ " integer null, " + COLUMN_NOTE + " text null);";

	public CalendarHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		log("Creating database");
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		log("Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		database.execSQL("drop table if exists " + TABLE_CALENDAR);
		onCreate(database);
	}
}
