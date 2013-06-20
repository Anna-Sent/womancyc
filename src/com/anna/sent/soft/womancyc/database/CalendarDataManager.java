package com.anna.sent.soft.womancyc.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.CalendarData;

public class CalendarDataManager {
	private Context mContext;

	public CalendarDataManager(Context context) {
		mContext = context;
	}

	private static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}

		return false;
	}

	private static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}

		return false;
	}

	public static String getBackupFileName() {
		String dir = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		if (dir.charAt(dir.length() - 1) != '/') {
			dir += "/";
		}

		return dir + "WomanCyc/backup.xml";
	}

	private static String TAG_ROW = "row";

	private String mErrorMessage = null;

	public String getErrorMessage() {
		return mErrorMessage;
	}

	public boolean backup(DataKeeper dataKeeper) {
		if (isExternalStorageWritable()) {
			try {
				File xmlfile = new File(getBackupFileName());
				xmlfile.getParentFile().mkdirs();
				FileOutputStream output = new FileOutputStream(xmlfile);

				XmlSerializer xmlSerializer = Xml.newSerializer();
				xmlSerializer.setOutput(output, "UTF-8");

				xmlSerializer.startDocument(null, true);
				writeCalendarTable(xmlSerializer, dataKeeper);
				xmlSerializer.endDocument();

				xmlSerializer.flush();
				output.close();

				return true;
			} catch (FileNotFoundException e) {
				mErrorMessage = mContext.getString(R.string.errorFileNotFound,
						getBackupFileName());
			} catch (IllegalArgumentException e) {
				mErrorMessage = mContext.getString(R.string.errorFileExport);
			} catch (IllegalStateException e) {
				mErrorMessage = mContext.getString(R.string.errorFileExport);
			} catch (IOException e) {
				mErrorMessage = mContext.getString(R.string.errorFileWriting);
			}
		} else {
			mErrorMessage = mContext
					.getString(R.string.errorExternalStorageWriting);
		}

		return false;
	}

	private static void writeCalendarTable(XmlSerializer xmlSerializer,
			DataKeeper dataKeeper) throws IllegalArgumentException,
			IllegalStateException, IOException {
		xmlSerializer.startTag(null, CalendarHelper.TABLE_CALENDAR);

		for (int i = 0; i < dataKeeper.getCount(); ++i) {
			CalendarData value = dataKeeper.get(i);

			xmlSerializer.startTag(null, TAG_ROW);
			xmlSerializer.attribute(null, CalendarHelper.COLUMN_ID,
					String.valueOf(value.getId()));
			xmlSerializer.attribute(null, CalendarHelper.COLUMN_MENSTRUATION,
					String.valueOf(value.getMenstruation()));
			xmlSerializer.attribute(null, CalendarHelper.COLUMN_SEX,
					String.valueOf(value.getSex()));
			xmlSerializer.attribute(null, CalendarHelper.COLUMN_TOOK_PILL,
					String.valueOf(value.getTookPill()));
			xmlSerializer.attribute(null, CalendarHelper.COLUMN_NOTE,
					value.getNote());
			xmlSerializer.endTag(null, TAG_ROW);
		}

		xmlSerializer.endTag(null, CalendarHelper.TABLE_CALENDAR);
	}

	public boolean restore(DataKeeper dataKeeper) {
		if (isExternalStorageReadable()) {
			try {
				File xmlfile = new File(getBackupFileName());
				xmlfile.getParentFile().mkdirs();
				FileReader input = new FileReader(xmlfile);

				XmlPullParser xpp = Xml.newPullParser();
				xpp.setInput(input);

				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					eventType = xpp.next();
					eventType = readCalendarTable(xpp, eventType, dataKeeper);
				}

				return true;
			} catch (FileNotFoundException e) {
				mErrorMessage = mContext.getString(R.string.errorFileNotFound,
						getBackupFileName());
			} catch (IOException e) {
				mErrorMessage = mContext.getString(R.string.errorFileReading);
			} catch (XmlPullParserException e) {
				mErrorMessage = mContext.getString(R.string.errorFileImport);
			}
		} else {
			mErrorMessage = mContext
					.getString(R.string.errorExternalStorageReading);
		}

		return false;
	}

	private static int readCalendarTable(XmlPullParser xpp, int eventType,
			DataKeeper dataKeeper) throws XmlPullParserException, IOException {
		if (eventType == XmlPullParser.START_TAG
				&& xpp.getName().equals(CalendarHelper.TABLE_CALENDAR)) {
			eventType = xpp.next();
			while (eventType == XmlPullParser.START_TAG
					&& xpp.getName().equals(TAG_ROW)) {
				Calendar date = Calendar.getInstance();
				int menstruation = 0;
				int sex = 0;
				boolean tookPill = false;
				String note = "";

				for (int i = 0; i < xpp.getAttributeCount(); ++i) {
					String name = xpp.getAttributeName(i);
					String value = xpp.getAttributeValue(i);
					if (name.equals(CalendarHelper.COLUMN_ID)) {
						date.setTimeInMillis(Long.valueOf(value));
					} else if (name.equals(CalendarHelper.COLUMN_MENSTRUATION)) {
						menstruation = Integer.valueOf(value);
					} else if (name.equals(CalendarHelper.COLUMN_SEX)) {
						sex = Integer.valueOf(value);
					} else if (name.equals(CalendarHelper.COLUMN_TOOK_PILL)) {
						tookPill = Boolean.valueOf(value);
					} else if (name.equals(CalendarHelper.COLUMN_NOTE)) {
						note = value;
					}
				}

				CalendarData value = new CalendarData(date);
				value.setMenstruation(menstruation);
				value.setSex(sex);
				value.setTookPill(tookPill);
				value.setNote(note);
				dataKeeper.insertOrUpdate(value);
				eventType = xpp.next();
				if (eventType == XmlPullParser.END_TAG
						&& xpp.getName().equals(TAG_ROW)) {
					eventType = xpp.next();
				} else {
					break;
				}
			}

			if (eventType == XmlPullParser.END_TAG
					&& xpp.getName().equals(CalendarHelper.TABLE_CALENDAR)) {
				eventType = xpp.next();
			}
		}

		return eventType;
	}
}
