package com.anna.sent.soft.womancyc.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.CalendarData;

public class CalendarDataManager {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private static String wrapMsg(String msg) {
		return CalendarDataManager.class.getSimpleName() + ": " + msg;
	}

	private static void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

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

	private static String TAG_ROW = "row";

	private String mErrorMessage = null;

	public String getErrorMessage() {
		return mErrorMessage;
	}

	/**
	 * 
	 * @param dataKeeper
	 *            not null
	 * @param filename
	 * @return
	 */
	public boolean backup(DataKeeper dataKeeper, String filename) {
		if (isExternalStorageWritable()) {
			try {
				File xmlfile = new File(filename);
				FileOutputStream output = new FileOutputStream(xmlfile);

				XmlSerializer xmlSerializer = Xml.newSerializer();
				xmlSerializer.setOutput(output, "UTF-8");
				xmlSerializer
						.setFeature(
								"http://xmlpull.org/v1/doc/features.html#indent-output",
								true);

				xmlSerializer.startDocument(null, true);
				writeCalendarTable(xmlSerializer, dataKeeper);
				xmlSerializer.endDocument();

				xmlSerializer.flush();
				output.close();

				return true;
			} catch (FileNotFoundException e) {
				mErrorMessage = mContext.getString(R.string.errorFileNotFound,
						filename);
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
					String.valueOf(value.getDateString()));
			xmlSerializer.attribute(null, CalendarHelper.COLUMN_MENSTRUATION,
					String.valueOf(value.getMenstruationString()));
			xmlSerializer.attribute(null, CalendarHelper.COLUMN_SEX,
					String.valueOf(value.getSexString()));
			xmlSerializer.attribute(null, CalendarHelper.COLUMN_TOOK_PILL,
					String.valueOf(value.getTookPillString()));
			xmlSerializer.attribute(null, CalendarHelper.COLUMN_NOTE,
					value.getNote());
			xmlSerializer.endTag(null, TAG_ROW);
		}

		xmlSerializer.endTag(null, CalendarHelper.TABLE_CALENDAR);
	}

	/**
	 * 
	 * @param dataKeeper
	 *            not null
	 * @param filename
	 * @return
	 */
	public boolean restore(DataKeeper dataKeeper, String filename) {
		if (isExternalStorageReadable()) {
			try {
				File xmlfile = new File(filename);
				FileReader input = new FileReader(xmlfile);

				XmlPullParser xpp = Xml.newPullParser();
				xpp.setInput(input);

				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					eventType = xpp.nextTag();
					eventType = readCalendarTable(xpp, eventType, dataKeeper);
				}

				return true;
			} catch (FileNotFoundException e) {
				mErrorMessage = mContext.getString(R.string.errorFileNotFound,
						filename);
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
			log("start of " + xpp.getName());
			eventType = xpp.nextTag();
			while (eventType == XmlPullParser.START_TAG
					&& xpp.getName().equals(TAG_ROW)) {
				log("start of " + xpp.getName());
				CalendarData data = new CalendarData();

				for (int i = 0; i < xpp.getAttributeCount(); ++i) {
					String name = xpp.getAttributeName(i);
					String value = xpp.getAttributeValue(i);
					if (name.equals(CalendarHelper.COLUMN_ID)) {
						log(name + " " + value);
						data.setDate(value);
					} else if (name.equals(CalendarHelper.COLUMN_MENSTRUATION)) {
						log(name + " " + value);
						data.setMenstruation(value);
					} else if (name.equals(CalendarHelper.COLUMN_SEX)) {
						log(name + " " + value);
						data.setSex(value);
					} else if (name.equals(CalendarHelper.COLUMN_TOOK_PILL)) {
						log(name + " " + value);
						data.setTookPill(value);
					} else if (name.equals(CalendarHelper.COLUMN_NOTE)) {
						log(name + " \"" + value + "\"");
						data.setNote(value);
					}
				}

				dataKeeper.insertOrUpdate(data);
				eventType = xpp.nextTag();
				if (eventType == XmlPullParser.END_TAG
						&& xpp.getName().equals(TAG_ROW)) {
					log("end of " + xpp.getName());
					eventType = xpp.nextTag();
				} else {
					break;
				}
			}

			if (eventType == XmlPullParser.END_TAG
					&& xpp.getName().equals(CalendarHelper.TABLE_CALENDAR)) {
				log("end of " + xpp.getName());
				eventType = xpp.next();
			}
		}

		return eventType;
	}
}
