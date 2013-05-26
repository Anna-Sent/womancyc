package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.fragments.DayViewFragment;
import com.anna.sent.soft.womancyc.fragments.MonthViewFragment;
import com.anna.sent.soft.womancyc.superclasses.DataKeeperActivity;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class MainActivity extends DataKeeperActivity implements
		MonthViewFragment.Listener, DayViewFragment.Listener {
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

	private final static String DAY_VIEW_TAG = DayViewFragment.class
			.getSimpleName();

	private MonthViewFragment mMonthView;
	private DayViewFragment mDayView;
	private boolean mIsLargeLayout;

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof MonthViewFragment) {
			mMonthView = (MonthViewFragment) fragment;
			mMonthView.setListener(this);
		}

		if (fragment instanceof DayViewFragment) {
			mDayView = (DayViewFragment) fragment;
			mDayView.setListener(this);
		}
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		super.setViews(savedInstanceState);
		setContentView(R.layout.activity_main);
		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);
	}

	@Override
	public void beforeOnSaveInstanceState() {
		FragmentManager fm = getSupportFragmentManager();

		Fragment dayView = fm.findFragmentById(R.id.dayView);
		if (dayView != null) {
			fm.beginTransaction().remove(dayView).commit();
		}

		dayView = fm.findFragmentByTag(DAY_VIEW_TAG);
		if (dayView != null) {
			fm.beginTransaction().remove(dayView).commit();
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
	protected void dataChanged() {
		if (mMonthView != null) {
			mMonthView.update();
		}
	}

	private void showAsDialogFragment(Calendar date) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		DialogFragment newFragment = createDayView(date);
		newFragment.show(fragmentManager, DAY_VIEW_TAG);
	}

	private void showAsEmbeddedFragment(Calendar date) {
		FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment dayView = fragmentManager.findFragmentById(R.id.dayView);
		if (dayView != null) {
			fragmentManager.beginTransaction().remove(dayView).commit();
		}

		fragmentManager.beginTransaction()
				.add(R.id.dayView, createDayView(date)).commit();
	}

	private DialogFragment createDayView(Calendar date) {
		CalendarData value = get(date);
		if (value == null) {
			value = new CalendarData(date);
		}

		Bundle args = new Bundle();
		args.putSerializable(value.getClass().getSimpleName(), value);

		DialogFragment newFragment = new DayViewFragment();
		newFragment.setArguments(args);

		return newFragment;
	}

	@Override
	public void onCalendarItemSelected(Calendar date) {
		if (mIsLargeLayout) {
			showAsEmbeddedFragment(date);
		}
	}

	@Override
	public void onCalendarItemLongClick(Calendar date) {
		if (!mIsLargeLayout) {
			showAsDialogFragment(date);
		}
	}

	@Override
	public void onCalendarItemChanged(Calendar date) {
		mMonthView.setSelectedDate(date);
	}
}