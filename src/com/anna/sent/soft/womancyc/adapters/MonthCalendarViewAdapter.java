package com.anna.sent.soft.womancyc.adapters;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.data.DataKeeper;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class MonthCalendarViewAdapter extends BaseAdapter {
	private static final String TAG = "moo";
	private static final boolean DEBUG = true;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	@SuppressWarnings("unused")
	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private final Context mContext;
	private final List<Calendar> mMonthCalendarValues = new ArrayList<Calendar>();
	private List<Integer> mDayOfWeekValues = new ArrayList<Integer>();
	private String[] mDayOfWeekNames;
	protected int mMonth, mYear;
	private Calendar mSelectedDate, mToday;

	private DataKeeper mDataKeeper = null;

	public MonthCalendarViewAdapter(Context context, DataKeeper dataKeeper) {
		super();
		mContext = context;
		mDataKeeper = dataKeeper;
		mToday = Calendar.getInstance();
		if (mToday.getFirstDayOfWeek() == Calendar.SUNDAY) {
			mDayOfWeekValues.add(Calendar.SUNDAY);
			mDayOfWeekValues.add(Calendar.MONDAY);
			mDayOfWeekValues.add(Calendar.TUESDAY);
			mDayOfWeekValues.add(Calendar.WEDNESDAY);
			mDayOfWeekValues.add(Calendar.THURSDAY);
			mDayOfWeekValues.add(Calendar.FRIDAY);
			mDayOfWeekValues.add(Calendar.SATURDAY);
		} else {
			mDayOfWeekValues.add(Calendar.MONDAY);
			mDayOfWeekValues.add(Calendar.TUESDAY);
			mDayOfWeekValues.add(Calendar.WEDNESDAY);
			mDayOfWeekValues.add(Calendar.THURSDAY);
			mDayOfWeekValues.add(Calendar.FRIDAY);
			mDayOfWeekValues.add(Calendar.SATURDAY);
			mDayOfWeekValues.add(Calendar.SUNDAY);
		}

		DateFormatSymbols symbols = new DateFormatSymbols();
		mDayOfWeekNames = symbols.getShortWeekdays();

		mSelectedDate = (Calendar) mToday.clone();
		int month = mToday.get(Calendar.MONTH);
		int year = mToday.get(Calendar.YEAR);
		initMonthCalendar(month, year);
	}

	public void setSelectedDate(Calendar value) {
		int year = value.get(Calendar.YEAR);
		int month = value.get(Calendar.MONTH);
		int day = value.get(Calendar.DAY_OF_MONTH);
		mSelectedDate.set(Calendar.YEAR, year);
		mSelectedDate.set(Calendar.MONTH, month);
		mSelectedDate.set(Calendar.DAY_OF_MONTH, day);
		if (mYear != year || mMonth != month) {
			initMonthCalendar(month, year);
		}

		notifyDataSetChanged();
	}

	public Calendar getSelectedDate() {
		return mSelectedDate;
	}

	@Override
	public Object getItem(int position) {
		if (position >= mDayOfWeekValues.size()) {
			return mMonthCalendarValues.get(position - mDayOfWeekValues.size());
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
		return mMonthCalendarValues.size() + mDayOfWeekValues.size();
	}

	private void initMonthCalendar(int month, int year) {
		mYear = year;
		mMonth = month;
		mMonthCalendarValues.clear();

		Calendar current = Calendar.getInstance();
		current.set(year, month, 1);

		Calendar prevMonth = (Calendar) current.clone();
		prevMonth.add(Calendar.MONTH, -1);

		Calendar nextMonth = (Calendar) current.clone();
		nextMonth.add(Calendar.MONTH, 1);

		int trailing = mDayOfWeekValues.indexOf(current
				.get(Calendar.DAY_OF_WEEK));
		int daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int i = 1; i <= trailing; ++i) {
			Calendar item = Calendar.getInstance();
			item.set(prevMonth.get(Calendar.YEAR),
					prevMonth.get(Calendar.MONTH), daysInPrevMonth - trailing
							+ i);
			mMonthCalendarValues.add(item);
		}

		int daysInCurrentMonth = current
				.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int i = 1; i <= daysInCurrentMonth; ++i) {
			Calendar item = Calendar.getInstance();
			item.set(year, month, i);
			mMonthCalendarValues.add(item);
		}

		int leading = mDayOfWeekValues.size() * 6 - mMonthCalendarValues.size();
		for (int i = 1; i <= leading; ++i) {
			Calendar item = Calendar.getInstance();
			item.set(nextMonth.get(Calendar.YEAR),
					nextMonth.get(Calendar.MONTH), i);
			mMonthCalendarValues.add(item);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View cell = null;

		if (position < mDayOfWeekValues.size()) {
			if (convertView != null
					&& convertView.getId() == getDayOfWeekViewId()) {
				cell = convertView;
			}

			if (cell == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				cell = inflater.inflate(getDayOfWeekLayoutResource(), parent,
						false);
			}

			initDayOfWeekItem(cell, position);
		} else if (mMonthCalendarValues.size() > 0) {
			if (convertView != null
					&& convertView.getId() == getDayOfMonthViewId()) {
				cell = convertView;
			}

			if (cell == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				cell = inflater.inflate(getDayOfMonthLayoutResource(), parent,
						false);
			}

			Calendar item = mMonthCalendarValues.get(position
					- mDayOfWeekValues.size());
			initDayOfMonth(cell, position, item);
		}

		return cell;
	}

	private int getDayOfWeekLayoutResource() {
		return R.layout.day_of_week;
	}

	private int getDayOfWeekViewId() {
		return R.id.dayOfWeek;
	}

	private void initDayOfWeekItem(View cell, int position) {
		TextView dayOfWeekTextView = (TextView) cell
				.findViewById(R.id.dayOfWeekTextView);
		dayOfWeekTextView.setText(mDayOfWeekNames[mDayOfWeekValues
				.get(position)]);
	}

	protected int getDayOfMonthLayoutResource() {
		return R.layout.day_of_month;
	}

	protected int getDayOfMonthViewId() {
		return R.id.dayOfMonth;
	}

	@SuppressWarnings("deprecation")
	protected void initDayOfMonth(View cell, int position, Calendar item) {
		Calendar begin = Calendar.getInstance();
		begin.set(2013, Calendar.MAY, 1);
		int dayOfCycle = DateUtils.getDifferenceInDays(item, begin);

		TextView dayOfCycleTextView = (TextView) cell
				.findViewById(R.id.dayOfCycleTextView);
		dayOfCycleTextView.setText(String.valueOf(dayOfCycle));

		TextView dayOfMonthTextView = (TextView) cell
				.findViewById(R.id.dayOfMonthTextView);
		dayOfMonthTextView.setText(String.valueOf(item
				.get(Calendar.DAY_OF_MONTH)));

		if (item.get(Calendar.MONTH) != mMonth) {
			dayOfCycleTextView.setTextColor(Color.rgb(0xff, 0xaa, 0x00));
			if (ThemeUtils.getThemeId(mContext) == ThemeUtils.DARK_THEME) {
				dayOfMonthTextView.setTextColor(Color.DKGRAY);
			} else {
				dayOfMonthTextView.setTextColor(Color.LTGRAY);
			}
		} else {
			dayOfCycleTextView.setTextColor(Color.rgb(0xff, 0x66, 0x00));
			if (ThemeUtils.getThemeId(mContext) == ThemeUtils.DARK_THEME) {
				dayOfMonthTextView.setTextColor(Color.WHITE);
			} else {
				dayOfMonthTextView.setTextColor(Color.BLACK);
			}
		}

		if (DateUtils.datesAreEqual(item, mToday)) {
			dayOfMonthTextView.setTextColor(Color.BLUE);
		}

		List<CalendarData> data = mDataKeeper.getData();
		int index = new DateUtils().indexOf(data, item);
		CalendarData cellData = null;
		if (index >= 0) {
			cellData = data.get(index);
		}

		List<Drawable> layers = new ArrayList<Drawable>();

		if (cellData != null) {
			switch (cellData.getMenstruation()) {
			case 1:
				layers.add(mContext.getResources().getDrawable(
						R.drawable.menstruation));
				break;
			case 2:
				layers.add(mContext.getResources().getDrawable(
						R.drawable.one_drop));
				break;
			case 3:
				layers.add(mContext.getResources().getDrawable(
						R.drawable.two_drops));
				break;
			case 4:
				layers.add(mContext.getResources().getDrawable(
						R.drawable.three_drops));
				break;
			}

			switch (cellData.getSex()) {
			case 1:
				layers.add(mContext.getResources().getDrawable(
						R.drawable.unprotected_sex));
				break;
			case 2:
				layers.add(mContext.getResources().getDrawable(
						R.drawable.protected_sex));
				break;
			}
		}

		if (DateUtils.datesAreEqual(item, mSelectedDate)) {
			layers.add(mContext.getResources().getDrawable(
					R.drawable.selected_day_of_month_bg));
		}

		LayerDrawable background = new LayerDrawable(
				layers.toArray(new Drawable[] {}));
		cell.setBackgroundDrawable(background);
	}
}