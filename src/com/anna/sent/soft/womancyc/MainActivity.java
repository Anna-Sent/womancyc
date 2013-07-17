package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.DatePicker;

import com.anna.sent.soft.womancyc.fragments.CalendarListener;
import com.anna.sent.soft.womancyc.fragments.DatePickerDialogFragment;
import com.anna.sent.soft.womancyc.fragments.DayViewFragment;
import com.anna.sent.soft.womancyc.fragments.MonthViewFragment;
import com.anna.sent.soft.womancyc.shared.Settings;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.OptionsActivity;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public class MainActivity extends OptionsActivity implements CalendarListener,
		OnDateSetListener {
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

	private MonthViewFragment mMonthView;
	private boolean mIsLargeLayout;

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof MonthViewFragment) {
			mMonthView = (MonthViewFragment) fragment;
			mMonthView.setListener(this);
			log("attach month view");
		}
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		super.setViews(savedInstanceState);
		setContentView(R.layout.activity_main);
		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);
		Settings.isBlocked(this, false);
	}

	private Calendar mDateToShow = null;

	@Override
	public void onStart() {
		log("onStart");
		super.onStart();
		if (mDateToShow == null) {
			mDateToShow = Calendar.getInstance();
		}

		mMonthView.setSelectedDate(mDateToShow);
		if (mIsLargeLayout) {
			showAsEmbeddedFragment(mDateToShow);
		}
	}

	public void onStop() {
		mDateToShow = mMonthView.getSelectedDate();
		super.onStop();
	}

	@Override
	public void restoreState(Bundle state) {
		mDateToShow = (Calendar) state.getSerializable(Shared.DATE_TO_SHOW);
	}

	@Override
	public void saveActivityState(Bundle state) {
		log("save " + DateUtils.toString(this, mMonthView.getSelectedDate()));
		state.putSerializable(Shared.DATE_TO_SHOW, mMonthView.getSelectedDate());
	}

	@Override
	public void beforeOnSaveInstanceState() {
		DayViewFragment dayView = getDayView();
		if (dayView != null) {
			FragmentManager fm = getSupportFragmentManager();
			fm.beginTransaction().remove(dayView).commit();
		}
	}

	@Override
	protected void dataChanged() {
		mMonthView.update();
		DayViewFragment dayView = getDayView();
		if (dayView != null) {
			dayView.update();
		}
	}

	private final static String TAG_DAY_VIEW = "day_view_dialog";
	private final static String TAG_DATE_PICKER = "date_picker_dialog";

	private void showAsDialogFragment(Calendar date) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		DialogFragment dayView = createDayView(date);
		dayView.show(fragmentManager, TAG_DAY_VIEW);
	}

	private void showAsEmbeddedFragment(Calendar date) {
		FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment dayView = fragmentManager.findFragmentById(R.id.dayView);
		if (dayView != null) {
			fragmentManager.beginTransaction().remove(dayView).commit();
		}

		dayView = createDayView(date);
		fragmentManager.beginTransaction().add(R.id.dayView, dayView).commit();
	}

	private DayViewFragment createDayView(Calendar date) {
		Bundle args = new Bundle();
		args.putSerializable(Shared.DATE_TO_SHOW, date);
		DayViewFragment newFragment = new DayViewFragment();
		newFragment.setArguments(args);
		newFragment.setListener(this);
		return newFragment;
	}

	@Override
	public void showDatePickerToChangeDate() {
		Bundle args = new Bundle();
		args.putSerializable(Shared.DATE_TO_SHOW, mMonthView.getSelectedDate());
		DatePickerDialogFragment dialog = new DatePickerDialogFragment();
		dialog.setArguments(args);
		dialog.setOnDateSetListener(this);
		dialog.show(getSupportFragmentManager(), TAG_DATE_PICKER);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar dateToShow = Calendar.getInstance();
		dateToShow.set(year, month, day);
		showDate(dateToShow);
	}

	@Override
	public void showDate(Calendar date) {
		if (!DateUtils.datesAreEqual(date, mMonthView.getSelectedDate())) {
			mMonthView.setSelectedDate(date);
			DayViewFragment dayView = getDayView();
			if (dayView != null) {
				dayView.setSelectedDate(date);
			}
		}
	}

	@Override
	public void showDetailedView(Calendar date) {
		if (!mIsLargeLayout) {
			showAsDialogFragment(date);
		}
	}

	private DayViewFragment getDayView() {
		FragmentManager fm = getSupportFragmentManager();
		DayViewFragment dayView = (DayViewFragment) fm
				.findFragmentByTag(TAG_DAY_VIEW);
		if (dayView == null) {
			dayView = (DayViewFragment) fm.findFragmentById(R.id.dayView);
		}

		return dayView;
	}
}