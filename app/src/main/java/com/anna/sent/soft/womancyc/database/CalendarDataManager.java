package com.anna.sent.soft.womancyc.database;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Xml;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.CalendarData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class CalendarDataManager {
    private static String TAG_ROW = "row";
    private Context mContext;
    private String mErrorMessage;

    public CalendarDataManager(Context context) {
        mContext = context;
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);

    }

    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);

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

    private static int readCalendarTable(XmlPullParser xpp, int eventType,
                                         DataKeeper dataKeeper) throws XmlPullParserException, IOException {
        if (eventType == XmlPullParser.START_TAG
                && xpp.getName().equals(CalendarHelper.TABLE_CALENDAR)) {
            eventType = xpp.nextTag();
            while (eventType == XmlPullParser.START_TAG
                    && xpp.getName().equals(TAG_ROW)) {
                CalendarData data = new CalendarData();

                for (int i = 0; i < xpp.getAttributeCount(); ++i) {
                    String name = xpp.getAttributeName(i);
                    String value = xpp.getAttributeValue(i);
                    switch (name) {
                        case CalendarHelper.COLUMN_ID:
                            data.setDate(value);
                            break;
                        case CalendarHelper.COLUMN_MENSTRUATION:
                            data.setMenstruation(value);
                            break;
                        case CalendarHelper.COLUMN_SEX:
                            data.setSex(value);
                            break;
                        case CalendarHelper.COLUMN_TOOK_PILL:
                            data.setTookPill(value);
                            break;
                        case CalendarHelper.COLUMN_NOTE:
                            data.setNote(value);
                            break;
                    }
                }

                dataKeeper.insertOrUpdate(data);
                eventType = xpp.nextTag();
                if (eventType == XmlPullParser.END_TAG
                        && xpp.getName().equals(TAG_ROW)) {
                    eventType = xpp.nextTag();
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

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public boolean backup(@NonNull DataKeeper dataKeeper, String filename) {
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

    public boolean restore(@NonNull DataKeeper dataKeeper, String filename) {
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
}
