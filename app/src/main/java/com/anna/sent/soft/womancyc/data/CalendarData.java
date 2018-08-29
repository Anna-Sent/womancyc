package com.anna.sent.soft.womancyc.data;

import android.text.TextUtils;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class CalendarData implements Serializable {
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String MENSTRUATION_YES = "yes";
    private static final String MENSTRUATION_ONE_DROP = "one_drop";
    private static final String MENSTRUATION_TWO_DROPS = "two_drops";
    private static final String MENSTRUATION_THREE_DROPS = "three_drops";
    private static final String MENSTRUATION_NO = "no";
    private static final String SEX_UNPROTECTED = "unprotected";
    private static final String SEX_PROTECTED = "protected";
    private static final String SEX_YES = "yes";
    private static final String SEX_NO = "no";
    private final static String TOOK_PILL_YES = "yes";
    private final static String TOOK_PILL_NO = "no";
    private LocalDate date;
    private int menstruation;
    private int sex;
    private boolean tookPill;
    private String note;

    public CalendarData() {
        this(LocalDate.now());
    }

    public CalendarData(LocalDate date) {
        this.date = date;
    }

    public CalendarData(CalendarData calendarData) {
        date = calendarData.date;
        menstruation = calendarData.menstruation;
        sex = calendarData.sex;
        tookPill = calendarData.tookPill;
        note = calendarData.note;
    }

    public long getId() {
        return date.getYear() * 10000 + date.getMonthOfYear() * 100 + date.getDayOfMonth();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(int value) {
        int year = value / 10000;
        value = value % 10000;
        int month = value / 100;
        int day = value % 100;
        date = new LocalDate(year, month, day);
    }

    public void setDate(String value) {
        String[] strings = value.split("-");
        if (strings.length == 3) {
            int day = Integer.parseInt(strings[0]);
            int month = Integer.parseInt(strings[1]);
            int year = Integer.parseInt(strings[2]);
            date = new LocalDate(year, month, day);
        }
    }

    public String getDateString() {
        return date.toString(DATE_FORMAT);
    }

    public int getMenstruation() {
        return menstruation;
    }

    public void setMenstruation(String value) {
        switch (value) {
            case MENSTRUATION_YES:
                menstruation = 1;
                break;
            case MENSTRUATION_ONE_DROP:
                menstruation = 2;
                break;
            case MENSTRUATION_TWO_DROPS:
                menstruation = 3;
                break;
            case MENSTRUATION_THREE_DROPS:
                menstruation = 4;
                break;
            default:
                menstruation = 0;
                break;
        }
    }

    public void setMenstruation(int value) {
        menstruation = value;
    }

    public String getMenstruationString() {
        switch (menstruation) {
            case 1:
                return MENSTRUATION_YES;
            case 2:
                return MENSTRUATION_ONE_DROP;
            case 3:
                return MENSTRUATION_TWO_DROPS;
            case 4:
                return MENSTRUATION_THREE_DROPS;
        }

        return MENSTRUATION_NO;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(String value) {
        switch (value) {
            case SEX_UNPROTECTED:
                sex = 1;
                break;
            case SEX_PROTECTED:
                sex = 2;
                break;
            case SEX_YES:
                sex = 3;
                break;
            default:
                sex = 0;
                break;
        }
    }

    public void setSex(int value) {
        sex = value;
    }

    public String getSexString() {
        switch (sex) {
            case 1:
                return SEX_UNPROTECTED;
            case 2:
                return SEX_PROTECTED;
            case 3:
                return SEX_YES;
        }

        return SEX_NO;
    }

    public String getNote() {
        return note == null ? "" : note;
    }

    public void setNote(String value) {
        note = value;
    }

    public boolean getTookPill() {
        return tookPill;
    }

    public void setTookPill(String value) {
        tookPill = value.equals(TOOK_PILL_YES);
    }

    public void setTookPill(boolean value) {
        tookPill = value;
    }

    public String getTookPillString() {
        return tookPill ? TOOK_PILL_YES : TOOK_PILL_NO;
    }

    public boolean isEmpty() {
        return menstruation == 0 && sex == 0 && !tookPill && !TextUtils.isEmpty(getNote());
    }

    @Override
    public String toString() {
        return getDateString()
                + ":: menstruation: " + getMenstruationString()
                + ", sex: " + getSexString()
                + ", took pill: " + getTookPillString()
                + ", note: \"" + getNote() + "\"";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        CalendarData value = (CalendarData) obj;
        return menstruation == value.menstruation
                && sex == value.sex
                && tookPill == value.tookPill
                && date.equals(value.date)
                && (getNote().equals(value.getNote()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + menstruation;
        result = prime * result + sex;
        result = prime * result + (tookPill ? 1 : 0);
        result = prime * result + date.hashCode();
        result = prime * result + getNote().hashCode();
        return result;
    }
}
