package com.anna.sent.soft.womancyc;

import org.joda.time.LocalDate;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
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

public class MainActivity extends OptionsActivity implements CalendarListener,
		OnDateSetListener {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	@SuppressWarnings("unused")
	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	@SuppressWarnings("unused")
	private void log(String msg, boolean debug) {
		if (DEBUG && debug) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private MonthViewFragment mMonthView;
	private boolean mIsLargeLayout;
	private FragmentManager mFragmentManager;

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof MonthViewFragment) {
			mMonthView = (MonthViewFragment) fragment;
			mMonthView.setListener(this);
			// log("attach month view");
		}
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		super.setViews(savedInstanceState);
		setContentView(R.layout.activity_main);
		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);
		Settings.isBlocked(this, false);
		mFragmentManager = getSupportFragmentManager();
	}

	private LocalDate mDateToShow = null;

	@Override
	public void onStart() {
		// log("onStart");
		super.onStart();
		if (mDateToShow == null) {
			mDateToShow = LocalDate.now();
		}

		// log("date is " + mDateToShow.toString());
		mMonthView.setSelectedDate(mDateToShow);
		if (mIsLargeLayout) {
			showAsEmbeddedFragment();
		}
	}

	public void onStop() {
		// log("onStop");
		mDateToShow = mMonthView.getSelectedDate();
		super.onStop();
	}

	@Override
	protected void onPause() {
		// log("onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		// log("onResume");
		super.onResume();
	}

	@Override
	protected void onRestart() {
		// log("onRestart");
		super.onRestart();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// log("onNewIntent");
		super.onNewIntent(intent);
	}

	@Override
	public void restoreState(Bundle state) {
		mDateToShow = (LocalDate) state.getSerializable(Shared.DATE_TO_SHOW);
		// log("restore " + (mDateToShow == null ? "null" :
		// mDateToShow.toString()));
	}

	@Override
	public void saveActivityState(Bundle state) {
		// log("save " + mMonthView.getSelectedDate().toString());
		state.putSerializable(Shared.DATE_TO_SHOW, mMonthView.getSelectedDate());
	}

	@Override
	public void beforeOnSaveInstanceState() {
		DayViewFragment dayView = getDayView();
		if (dayView != null) {
			mFragmentManager.beginTransaction().remove(dayView).commit();
		}

		DatePickerDialogFragment datePickerDialog = getDatePickerDialog();
		if (datePickerDialog != null) {
			mFragmentManager.beginTransaction().remove(datePickerDialog)
					.commit();
		}
	}

	@Override
	protected void dataChanged() {
		// log("data changed");
		mMonthView.update();
		DayViewFragment dayView = getDayView();
		if (dayView != null) {
			dayView.update();
		}
	}

	private final static String TAG_DAY_VIEW = "day_view_dialog";
	private final static String TAG_DATE_PICKER = "date_picker_dialog";

	private void showAsDialogFragment() {
		DialogFragment dayView = createDayView();
		dayView.show(mFragmentManager, TAG_DAY_VIEW);
	}

	private void showAsEmbeddedFragment() {
		Fragment dayView = mFragmentManager.findFragmentById(R.id.dayView);
		if (dayView != null) {
			mFragmentManager.beginTransaction().remove(dayView).commit();
		}

		dayView = createDayView();
		mFragmentManager.beginTransaction().add(R.id.dayView, dayView).commit();
	}

	private DayViewFragment createDayView() {
		Bundle args = new Bundle();
		args.putSerializable(Shared.DATE_TO_SHOW, mMonthView.getSelectedDate());
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
		dialog.show(mFragmentManager, TAG_DATE_PICKER);
	}

	private DatePickerDialogFragment getDatePickerDialog() {
		return (DatePickerDialogFragment) mFragmentManager
				.findFragmentByTag(TAG_DATE_PICKER);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		LocalDate dateToShow = new LocalDate(year, month + 1, day);
		navigateToDate(dateToShow);
	}

	@Override
	public void navigateToDate(LocalDate date) {
		if (!date.isEqual(mMonthView.getSelectedDate())) {
			// log("navigate to date");
			mMonthView.setSelectedDate(date);
			setDayViewToDate();
		}
	}

	@Override
	public void showDetailedView() {
		if (!mIsLargeLayout) {
			showAsDialogFragment();
		}
	}

	@Override
	public void updateDetailedView() {
		if (mIsLargeLayout) {
			setDayViewToDate();
		}
	}

	private void setDayViewToDate() {
		DayViewFragment dayView = getDayView();
		if (dayView != null) {
			dayView.setSelectedDate(mMonthView.getSelectedDate());
		}
	}

	private DayViewFragment getDayView() {
		DayViewFragment dayView = (DayViewFragment) mFragmentManager
				.findFragmentByTag(TAG_DAY_VIEW);
		if (dayView == null) {
			dayView = (DayViewFragment) mFragmentManager
					.findFragmentById(R.id.dayView);
		}

		return dayView;
	}
}