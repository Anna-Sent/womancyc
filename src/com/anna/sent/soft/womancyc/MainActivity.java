package com.anna.sent.soft.womancyc;

import java.util.List;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.data.DataKeeper;
import com.anna.sent.soft.womancyc.database.CalendarDataSource;
import com.anna.sent.soft.womancyc.fragments.MonthCalendarViewFragment;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.StateSaverActivity;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class MainActivity extends StateSaverActivity implements DataKeeper {
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

	private CalendarDataSource mDataSource;
	private List<CalendarData> mValues;
	private MonthCalendarViewFragment mCalendar;

	@Override
	public void setViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);

		mDataSource = new CalendarDataSource(this);
		mDataSource.open();
		mValues = mDataSource.getAll();
	}

	@Override
	protected void onResume() {
		try {
			mDataSource.open();
			mValues = mDataSource.getAll();
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
		Toast.makeText(this, "negative", Toast.LENGTH_SHORT).show();
	}

	private void updateCalendar() {
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
}
