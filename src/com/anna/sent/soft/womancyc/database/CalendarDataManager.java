package com.anna.sent.soft.womancyc.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Xml;

import com.anna.sent.soft.womancyc.data.CalendarData;

public class CalendarDataManager {
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

	public static void backup(DataKeeperInterface dataKeeper) {
		if (isExternalStorageWritable()) {
			try {
				File xmlfile = new File(
						Environment.getExternalStorageDirectory() + "/sos.xml");
				xmlfile.getParentFile().mkdirs();
				FileOutputStream output = new FileOutputStream(xmlfile);

				XmlSerializer xmlSerializer = Xml.newSerializer();
				xmlSerializer.setOutput(output, "UTF-8");

				xmlSerializer.startDocument(null, true);
				xmlSerializer.startTag(null, CalendarHelper.TABLE_CALENDAR);

				for (int i = 0; i < dataKeeper.getCount(); ++i) {
					CalendarData value = dataKeeper.get(i);

					xmlSerializer.startTag(null, "row");
					xmlSerializer.attribute(null, CalendarHelper.COLUMN_ID,
							String.valueOf(value.getId()));
					xmlSerializer.attribute(null,
							CalendarHelper.COLUMN_MENSTRUATION,
							String.valueOf(value.getMenstruation()));
					xmlSerializer.attribute(null, CalendarHelper.COLUMN_SEX,
							String.valueOf(value.getSex()));
					xmlSerializer.attribute(null,
							CalendarHelper.COLUMN_TOOK_PILL,
							String.valueOf(value.getTookPill()));
					xmlSerializer.attribute(null, CalendarHelper.COLUMN_NOTE,
							value.getNote());
					xmlSerializer.endTag(null, "row");
				}

				xmlSerializer.endTag(null, CalendarHelper.TABLE_CALENDAR);
				xmlSerializer.endDocument();

				xmlSerializer.flush();
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void restore(DataKeeperInterface dataKeeper) {
		if (isExternalStorageReadable()) {
			try {
				File xmlfile = new File(
						Environment.getExternalStorageDirectory() + "/sos.xml");
				xmlfile.getParentFile().mkdirs();
				FileReader input = new FileReader(xmlfile);

				XmlPullParser xpp = Xml.newPullParser();
				xpp.setInput(input);

				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG
							&& xpp.getName().equals(
									CalendarHelper.TABLE_CALENDAR)) {
						eventType = xpp.next();
						while (eventType == XmlPullParser.START_TAG
								&& xpp.getName().equals("row")) {
							Calendar date = Calendar.getInstance();
							int menstruation = 0;
							int sex = 0;
							boolean tookPill = false;
							String note = "";

							for (int i = 0; i < xpp.getAttributeCount(); ++i) {
								if (xpp.getAttributeName(i).equals(
										CalendarHelper.COLUMN_ID)) {
									date.setTimeInMillis(Long.valueOf(xpp
											.getAttributeValue(i)));
								} else if (xpp.getAttributeName(i).equals(
										CalendarHelper.COLUMN_MENSTRUATION)) {
									menstruation = Integer.valueOf(xpp
											.getAttributeValue(i));
								} else if (xpp.getAttributeName(i).equals(
										CalendarHelper.COLUMN_SEX)) {
									sex = Integer.valueOf(xpp
											.getAttributeValue(i));
								} else if (xpp.getAttributeName(i).equals(
										CalendarHelper.COLUMN_TOOK_PILL)) {
									tookPill = Boolean.valueOf(xpp
											.getAttributeValue(i));
								} else if (xpp.getAttributeName(i).equals(
										CalendarHelper.COLUMN_NOTE)) {
									note = xpp.getAttributeValue(i);
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
									&& xpp.getName().equals("row")) {
								eventType = xpp.next();
							} else {
								break;
							}
						}
					}

					eventType = xpp.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
