package com.anna.sent.soft.womancyc.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.Calculator;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.shared.Settings;
import com.anna.sent.soft.womancyc.utils.MyLog;

import org.joda.time.LocalDate;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthViewAdapter extends BaseAdapter implements OnClickListener, OnLongClickListener {
    private final Context mContext;
    private final List<LocalDate> mMonthCalendarValues = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private int mMonth, mYear;
    private int[] mDayOfWeekValues = new int[7];
    private String[] mDayOfWeekNames;
    private LocalDate mSelectedDate, mToday;
    private View mSelectedView = null;
    private int mThemeId;
    private DataKeeper mDataKeeper;
    private Calculator mCalculator;
    private Listener mListener;
    private SparseArray<Drawable> mDrawablesFromTheme = new SparseArray<>();
    private SparseArray<Drawable> mDrawables = new SparseArray<>();

    public MonthViewAdapter(Context context, @NonNull DataKeeper dataKeeper, Listener listener) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mThemeId = Settings.settingsTheme.getTheme(mContext);
        mDataKeeper = dataKeeper;
        mListener = listener;
        mCalculator = new Calculator(context, mDataKeeper);
        mToday = LocalDate.now();
        if (Calendar.getInstance().getFirstDayOfWeek() == Calendar.SUNDAY) {
            mDayOfWeekValues[0] = Calendar.SUNDAY;
            mDayOfWeekValues[1] = Calendar.MONDAY;
            mDayOfWeekValues[2] = Calendar.TUESDAY;
            mDayOfWeekValues[3] = Calendar.WEDNESDAY;
            mDayOfWeekValues[4] = Calendar.THURSDAY;
            mDayOfWeekValues[5] = Calendar.FRIDAY;
            mDayOfWeekValues[6] = Calendar.SATURDAY;
        } else {
            mDayOfWeekValues[0] = Calendar.MONDAY;
            mDayOfWeekValues[1] = Calendar.TUESDAY;
            mDayOfWeekValues[2] = Calendar.WEDNESDAY;
            mDayOfWeekValues[3] = Calendar.THURSDAY;
            mDayOfWeekValues[4] = Calendar.FRIDAY;
            mDayOfWeekValues[5] = Calendar.SATURDAY;
            mDayOfWeekValues[6] = Calendar.SUNDAY;
        }

        DateFormatSymbols symbols = new DateFormatSymbols();
        mDayOfWeekNames = symbols.getShortWeekdays();

        mSelectedDate = mToday;
        int month = mToday.getMonthOfYear();
        int year = mToday.getYear();
        initMonthCalendar(month, year);
    }

    private String wrapMsg(String msg) {
        return getClass().getSimpleName() + ": " + msg;
    }

    private void log(String msg) {
        MyLog.getInstance().logcat(Log.DEBUG, wrapMsg(msg));
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public LocalDate getSelectedDate() {
        return mSelectedDate;
    }

    public void setSelectedDate(LocalDate value) {
        int year = value.getYear();
        int month = value.getMonthOfYear();
        mSelectedDate = value;
        if (mYear != year || mMonth != month) {
            initMonthCalendar(month, year);
        }

        mSelectedView = null;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 7) {
            return mMonthCalendarValues.get(position - 7);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mMonthCalendarValues.size() + 7;
    }

    private int indexOfDayOfWeek(Calendar date) {
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        int i = 0;
        while (i < 7 && mDayOfWeekValues[i] != dayOfWeek) {
            ++i;
        }

        return i;
    }

    private void initMonthCalendar(final int month, final int year) {
        mYear = year;
        mMonth = month;
        mMonthCalendarValues.clear();

        Calendar current = Calendar.getInstance();
        current.set(year, month - 1, 1);

        Calendar prevMonth = (Calendar) current.clone();
        prevMonth.add(Calendar.MONTH, -1);

        Calendar nextMonth = (Calendar) current.clone();
        nextMonth.add(Calendar.MONTH, 1);

        int trailing = indexOfDayOfWeek(current);
        int daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= trailing; ++i) {
            LocalDate item = new LocalDate(prevMonth.get(Calendar.YEAR),
                    prevMonth.get(Calendar.MONTH) + 1,
                    daysInPrevMonth - trailing + i);
            mMonthCalendarValues.add(item);
        }

        int daysInCurrentMonth = current.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= daysInCurrentMonth; ++i) {
            LocalDate item = new LocalDate(year, month, i);
            mMonthCalendarValues.add(item);
        }

        int leading = 42 - mMonthCalendarValues.size();
        for (int i = 1; i <= leading; ++i) {
            LocalDate item = new LocalDate(nextMonth.get(Calendar.YEAR),
                    nextMonth.get(Calendar.MONTH) + 1, i);
            mMonthCalendarValues.add(item);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = null;

        if (position < 7) {
            if (convertView != null && convertView.getId() == getDayOfWeekViewId()) {
                cell = convertView;
            }

            if (cell == null) {
                cell = mLayoutInflater.inflate(getDayOfWeekLayoutResource(), parent, false);
            }

            initDayOfWeekItem(cell, position);
        } else {
            if (convertView != null && convertView.getId() == getDayOfMonthViewId()) {
                cell = convertView;
            }

            if (cell == null) {
                cell = mLayoutInflater.inflate(getDayOfMonthLayoutResource(), parent, false);
            }

            LocalDate item = mMonthCalendarValues.get(position - 7);
            initDayOfMonth(cell, item);
        }

        return cell;
    }

    private int getDayOfWeekLayoutResource() {
        return R.layout.grid_cell_day_of_week;
    }

    private int getDayOfWeekViewId() {
        return R.id.dayOfWeek;
    }

    private void initDayOfWeekItem(View cell, int position) {
        TextView dayOfWeekTextView = cell.findViewById(R.id.dayOfWeekTextView);
        dayOfWeekTextView.setText(mDayOfWeekNames[mDayOfWeekValues[position]]);
    }

    private int getDayOfMonthLayoutResource() {
        return R.layout.grid_cell_day_of_month;
    }

    private int getDayOfMonthViewId() {
        return R.id.dayOfMonth;
    }

    private void initDayOfMonth(View cell, LocalDate item) {
        cell.setTag(item);

        CalendarData cellData = mDataKeeper.get(item);
        int dayOfCycle = mCalculator.getDayOfCycle(item);

        TextView dayOfCycleTextView = cell.findViewById(R.id.dayOfCycleTextView);
        dayOfCycleTextView.setText(dayOfCycle <= 0 ? "" : String.valueOf(dayOfCycle));

        TextView dayOfMonthTextView = cell.findViewById(R.id.dayOfMonthTextView);
        dayOfMonthTextView.setText(String.valueOf(item.getDayOfMonth()));

        LocalDate expectedFirstDayOfNextCycle = mCalculator
                .getExpectedFirstDayOfNextCycle(mToday);
        if (dayOfCycle > 0
                && (expectedFirstDayOfNextCycle != null
                && item.isBefore(expectedFirstDayOfNextCycle)
                || item.isBefore(mToday) || item.equals(mToday))) {
            // orange, current and past cycles
            dayOfCycleTextView.setTextColor(Color.rgb(0xff, 0xaa, 0x00));
        } else {
            // yellow, future cycles
            dayOfCycleTextView.setTextColor(Color.rgb(0xff, 0xdd, 0x00));
        }

        if (item.getMonthOfYear() == mMonth) {
            if (Settings.settingsTheme.isDefaultTheme(mContext, mThemeId)) {
                dayOfMonthTextView.setTextColor(Color.WHITE);
            } else {
                dayOfMonthTextView.setTextColor(Color.BLACK);
            }
        } else {
            if (Settings.settingsTheme.isDefaultTheme(mContext, mThemeId)) {
                dayOfMonthTextView.setTextColor(Color.DKGRAY);
            } else {
                dayOfMonthTextView.setTextColor(Color.LTGRAY);
            }
        }

        if (item.isEqual(mToday)) {
            dayOfMonthTextView.setTextColor(Color.BLUE);
        }

        List<Drawable> layers = new ArrayList<>();

        if (expectedFirstDayOfNextCycle != null
                && (item.isAfter(expectedFirstDayOfNextCycle)
                && dayOfCycle == 1 || item.equals(expectedFirstDayOfNextCycle))) {
            layers.add(getDrawable(R.drawable.bg_menstruation_expected));
        }

        if (cellData != null) {
            switch (cellData.getMenstruation()) {
                case 1:
                    layers.add(getDrawable(R.drawable.bg_menstruation));
                    break;
                case 2:
                    layers.add(getDrawable(R.drawable.bg_one_drop));
                    break;
                case 3:
                    layers.add(getDrawable(R.drawable.bg_two_drops));
                    break;
                case 4:
                    layers.add(getDrawable(R.drawable.bg_three_drops));
                    break;
            }

            switch (cellData.getSex()) {
                case 1:
                    layers.add(getDrawableFromTheme(R.attr.unprotected_sex_bg));
                    break;
                case 2:
                    layers.add(getDrawableFromTheme(R.attr.protected_sex_bg));
                    break;
                case 3:
                    layers.add(getDrawableFromTheme(R.attr.sex_bg));
                    break;
            }

            if (!TextUtils.isEmpty(cellData.getNote())) {
                layers.add(getDrawableFromTheme(R.attr.note_bg));
            }

            if (cellData.getTookPill()) {
                layers.add(getDrawableFromTheme(R.attr.took_pill_bg));
            }
        }

        if (item.isEqual(mSelectedDate)) {
            layers.add(getDrawable(R.drawable.bg_selected_view));
            mSelectedView = cell;
        }

        LayerDrawable background = layers.isEmpty()
                ? null
                : new LayerDrawable(layers.toArray(new Drawable[layers.size()]));

        cell.setBackgroundDrawable(background);
        cell.setOnClickListener(this);
        cell.setOnLongClickListener(this);
    }

    private Drawable getDrawableFromTheme(int attribute) {
        Drawable result = mDrawablesFromTheme.get(attribute);
        if (result == null) {
            int[] attrs = new int[]{attribute};
            TypedArray ta = mContext.obtainStyledAttributes(attrs);
            result = ta.getDrawable(0);
            ta.recycle();

            mDrawablesFromTheme.put(attribute, result);
        }

        return result;
    }

    private Drawable getDrawable(int drawable) {
        Drawable result = mDrawables.get(drawable);
        if (result == null) {
            Resources resources = mContext.getResources();
            result = resources.getDrawable(drawable);

            mDrawables.put(drawable, result);
        }

        return result;
    }

    public void update() {
        log("update");
        mCalculator = new Calculator(mContext, mDataKeeper);
        mSelectedView = null;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        log("onClick");
        mSelectedDate = (LocalDate) v.getTag();
        if (mSelectedView != null) {
            initDayOfMonth(mSelectedView, (LocalDate) mSelectedView.getTag());
        }

        initDayOfMonth(v, mSelectedDate);

        if (mListener != null) {
            mListener.onItemClick();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        log("onLongClick");
        onClick(v);

        if (mListener != null) {
            mListener.onItemLongClick();
        }

        return true;
    }

    public interface Listener {
        void onItemClick();

        void onItemLongClick();
    }
}
