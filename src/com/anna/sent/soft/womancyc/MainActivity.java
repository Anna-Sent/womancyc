package com.anna.sent.soft.womancyc;

import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog.OnDateSetListener;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.data.DataKeeper;
import com.anna.sent.soft.womancyc.database.CalendarDataSource;
import com.anna.sent.soft.womancyc.fragments.DatePickerDialogFragment;
import com.anna.sent.soft.womancyc.fragments.MonthCalendarViewFragment;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.StateSaverActivity;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class MainActivity extends StateSaverActivity implements DataKeeper,
		OnClickListener, OnDateSetListener {
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

	private CalendarDataSource mDataSource;
	private List<CalendarData> mValues;
	private List<String> mNotes;
	private MonthCalendarViewFragment mCalendar;

	@Override
	public void setViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);

		mDataSource = new CalendarDataSource(this);
		mDataSource.open();
		mValues = mDataSource.getAllRows();
		updateNotes();
	}

	@Override
	protected void onResume() {
		try {
			mDataSource.open();
			mValues = mDataSource.getAllRows();
			updateNotes();
		} catch (SQLException e) {
			e.printStackTrace();
			Toast.makeText(this,
					getString(R.string.errorWhileOpenningDatabase),
					Toast.LENGTH_LONG).show();
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mDataSource.close();
	}

	@Override
	public void beforeOnSaveInstanceState() {
		FragmentManager fm = getSupportFragmentManager();
		Fragment details = fm.findFragmentById(R.id.editor);
		if (details != null) {
			fm.beginTransaction().remove(details).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		switch (ThemeUtils.getThemeId(this)) {
		case ThemeUtils.LIGHT_THEME:
			menu.findItem(R.id.lighttheme).setChecked(true);
			break;
		case ThemeUtils.DARK_THEME:
			menu.findItem(R.id.darktheme).setChecked(true);
			break;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.lighttheme:
			ThemeUtils.changeToTheme(this, ThemeUtils.LIGHT_THEME);
			return true;
		case R.id.darktheme:
			ThemeUtils.changeToTheme(this, ThemeUtils.DARK_THEME);
			return true;
		case R.id.help:
			/*
			 * Intent intent = new Intent(); intent.setClass(this,
			 * HelpActivity.class);
			 * 
			 * MainActivityStateSaver.save(this, intent);
			 * 
			 * startActivity(intent); return true;
			 */
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public List<CalendarData> getData() {
		return mValues;
	}

	@Override
	public List<String> getNotes() {
		return mNotes;
	}

	private void updateNotes() {
		mNotes = mDataSource.getAllNotes();
	}

	@Override
	public void insertOrUpdate(CalendarData value) {
		int index = new DateUtils().indexOf(mValues, value);
		if (index >= 0) {
			mDataSource.update(value);
			mValues.set(index, value);
		} else {
			mDataSource.insert(value);
			mValues.add(-index - 1, value);
		}

		updateCalendar();
	}

	@Override
	public void delete(CalendarData value) {
		int index = new DateUtils().indexOf(mValues, value);
		if (index >= 0) {
			mDataSource.delete(value);
			mValues.remove(index);

			updateCalendar();
		}
	}

	@Override
	public void cancel(CalendarData value) {
	}

	private void updateCalendar() {
		updateNotes();
		if (mCalendar != null) {
			mCalendar.update();
		}
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof MonthCalendarViewFragment) {
			mCalendar = (MonthCalendarViewFragment) fragment;
		}
	}

	private void toPrevMonth() {
		Calendar dateToShow = (Calendar) mCalendar.getSelectedDate().clone();
		dateToShow.set(Calendar.DAY_OF_MONTH, 1);
		dateToShow.add(Calendar.MONTH, -1);
		mCalendar.setSelectedDate(dateToShow);
	}

	private void toNextMonth() {
		Calendar dateToShow = (Calendar) mCalendar.getSelectedDate().clone();
		dateToShow.set(Calendar.DAY_OF_MONTH, 1);
		dateToShow.add(Calendar.MONTH, 1);
		mCalendar.setSelectedDate(dateToShow);
	}

	private void toPrevDay() {
		Calendar dateToShow = (Calendar) mCalendar.getSelectedDate().clone();
		dateToShow.add(Calendar.DAY_OF_MONTH, -1);
		mCalendar.setSelectedDate(dateToShow);
	}

	private void toNextDay() {
		Calendar dateToShow = (Calendar) mCalendar.getSelectedDate().clone();
		dateToShow.add(Calendar.DAY_OF_MONTH, 1);
		mCalendar.setSelectedDate(dateToShow);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.prevMonth:
			toPrevMonth();
			break;
		case R.id.nextMonth:
			toNextMonth();
			break;
		case R.id.currentMonth:
			Bundle args = new Bundle();
			args.putSerializable(Shared.DATE_TO_SHOW,
					mCalendar.getSelectedDate());
			DatePickerDialogFragment dialog = new DatePickerDialogFragment();
			dialog.setArguments(args);
			dialog.setOnDateSetListener(this);
			dialog.show(getSupportFragmentManager(), dialog.getClass()
					.getSimpleName());
			break;
		case R.id.nextDay:
			toNextDay();
			break;
		case R.id.prevDay:
			toPrevDay();
			break;
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar dateToShow = Calendar.getInstance();
		dateToShow.set(year, month, day);
		mCalendar.setSelectedDate(dateToShow);
	}
}
