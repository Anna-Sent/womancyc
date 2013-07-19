package com.anna.sent.soft.womancyc.adapters;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class MonthViewAdapter extends BaseAdapter implements OnClickListener,
		OnLongClickListener {
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

	private final Context mContext;
	private final List<Calendar> mMonthCalendarValues = new ArrayList<Calendar>();
	private List<Integer> mDayOfWeekValues = new ArrayList<Integer>();
	private String[] mDayOfWeekNames;
	protected int mMonth, mYear;
	private Calendar mSelectedDate, mToday;
	private View mSelectedView = null;
	private int mThemeId;

	/**
	 * Must be not null.
	 */
	private DataKeeper mDataKeeper;
	private Calculator mCalculator;

	public interface Listener {
		public void onItemClick();

		public void onItemLongClick();
	}

	private Listener mListener;

	public MonthViewAdapter(Context context, DataKeeper dataKeeper,
			Listener listener) {
		super();
		mContext = context;
		mThemeId = ThemeUtils.getThemeId(mContext);
		mDataKeeper = dataKeeper;
		mListener = listener;
		mCalculator = new Calculator(context, mDataKeeper);
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
		mSelectedDate = value;
		if (mYear != year || mMonth != month) {
			initMonthCalendar(month, year);
		}

		mSelectedView = null;
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
		TextView dayOfWeekTextView = (TextView) cell
				.findViewById(R.id.dayOfWeekTextView);
		dayOfWeekTextView.setText(mDayOfWeekNames[mDayOfWeekValues
				.get(position)]);
	}

	protected int getDayOfMonthLayoutResource() {
		return R.layout.grid_cell_day_of_month;
	}

	protected int getDayOfMonthViewId() {
		return R.id.dayOfMonth;
	}

	@SuppressWarnings("deprecation")
	protected void initDayOfMonth(View cell, Calendar item) {
		cell.setTag(item);

		CalendarData cellData = mDataKeeper.get(item);
		int dayOfCycle = mCalculator.getDayOfCycle(item);

		TextView dayOfCycleTextView = (TextView) cell
				.findViewById(R.id.dayOfCycleTextView);
		dayOfCycleTextView.setText(dayOfCycle <= 0 ? "" : String
				.valueOf(dayOfCycle));

		TextView dayOfMonthTextView = (TextView) cell
				.findViewById(R.id.dayOfMonthTextView);
		dayOfMonthTextView.setText(String.valueOf(item
				.get(Calendar.DAY_OF_MONTH)));

		if (item.get(Calendar.MONTH) != mMonth) {
			dayOfCycleTextView.setTextColor(Color.rgb(0xff, 0xdd, 0x00));
			if (mThemeId == ThemeUtils.DARK_THEME) {
				dayOfMonthTextView.setTextColor(Color.DKGRAY);
			} else {
				dayOfMonthTextView.setTextColor(Color.LTGRAY);
			}
		} else {
			dayOfCycleTextView.setTextColor(Color.rgb(0xff, 0xaa, 0x00));
			if (mThemeId == ThemeUtils.DARK_THEME) {
				dayOfMonthTextView.setTextColor(Color.WHITE);
			} else {
				dayOfMonthTextView.setTextColor(Color.BLACK);
			}
		}

		if (DateUtils.datesAreEqual(item, mToday)) {
			dayOfMonthTextView.setTextColor(Color.BLUE);
		}

		List<Drawable> layers = new ArrayList<Drawable>();

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

			if (cellData.getNote() != null && !cellData.getNote().equals("")) {
				layers.add(getDrawableFromTheme(R.attr.note_bg));
			}

			if (cellData.getTookPill()) {
				layers.add(getDrawableFromTheme(R.attr.took_pill_bg));
			}
		}

		if (DateUtils.datesAreEqual(item, mSelectedDate)) {
			layers.add(getDrawable(R.drawable.bg_selected_view));
			mSelectedView = cell;
		}

		LayerDrawable background = null;
		if (layers.size() > 0) {
			background = new LayerDrawable(layers.toArray(new Drawable[] {}));
		}

		cell.setBackgroundDrawable(background);
		cell.setOnClickListener(this);
		cell.setOnLongClickListener(this);
	}

	private SparseArray<Drawable> mDrawablesFromTheme = new SparseArray<Drawable>();

	private Drawable getDrawableFromTheme(int attribute) {
		Drawable result = mDrawablesFromTheme.get(attribute);
		if (result == null) {
			int[] attrs = new int[] { attribute };
			TypedArray ta = mContext.obtainStyledAttributes(attrs);
			result = ta.getDrawable(0);
			ta.recycle();

			mDrawablesFromTheme.put(attribute, result);
		}

		return result;
	}

	private SparseArray<Drawable> mDrawables = new SparseArray<Drawable>();

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
		mSelectedDate = (Calendar) v.getTag();
		if (mSelectedView != null) {
			initDayOfMonth(mSelectedView, (Calendar) mSelectedView.getTag());
		}

		initDayOfMonth(v, mSelectedDate);

		if (mListener != null) {
			mListener.onItemClick();
		}
	}

	@Override
	public boolean onLongClick(View v) {
		onClick(v);

		if (mListener != null) {
			mListener.onItemLongClick();
		}

		return true;
	}
}