package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.fragments.DayViewFragment;
import com.anna.sent.soft.womancyc.fragments.MonthViewFragment;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.DataKeeperActivity;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class MainActivity extends DataKeeperActivity implements
		MonthViewFragment.Listener, DayViewFragment.Listener {
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

	private Calendar mDateToShow = null;

	@Override
	public void onStart() {
		log("onStart");
		super.onStart();
		if (mDateToShow == null) {
			mDateToShow = Calendar.getInstance();
		}

		showDate(mDateToShow);
	}

	public void onStop() {
		mDateToShow = mMonthView.getSelectedDate();
		super.onStop();
	}

	@Override
	public void restoreState(Bundle state) {
		mDateToShow = (Calendar) state.getSerializable(Shared.DATE_TO_SHOW);
		log("restore " + DateUtils.toString(this, mDateToShow));
	}

	@Override
	public void saveActivityState(Bundle state) {
		log("save " + DateUtils.toString(this, mMonthView.getSelectedDate()));
		state.putSerializable(Shared.DATE_TO_SHOW, mMonthView.getSelectedDate());
	}

	@Override
	public void beforeOnSaveInstanceState() {
		FragmentManager fm = getSupportFragmentManager();

		Fragment dayView = fm.findFragmentById(R.id.dayView);
		if (dayView != null) {
			fm.beginTransaction().remove(dayView).commit();
		}
	}

	@Override
	protected void dataChanged() {
		if (mMonthView != null) {
			mMonthView.update();
		}
	}

	private final static int REQUEST_DATE = 1;

	private void showAsDialogFragment(Calendar date) {
		Intent intent = new Intent(
				this,
				ThemeUtils.DARK_THEME == ThemeUtils.getThemeId(this) ? DayViewActivityDark.class
						: DayViewActivityLight.class);
		intent.putExtra(Shared.DATE_TO_SHOW, date);
		startActivityForResult(intent, REQUEST_DATE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_DATE && resultCode == Activity.RESULT_OK) {
			Calendar date = (Calendar) data
					.getSerializableExtra(Shared.DATE_TO_SHOW);
			log("got from result " + DateUtils.toString(this, date));
			showDate(date);
		}
	}

	private void showAsEmbeddedFragment(Calendar date) {
		FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment dayView = fragmentManager.findFragmentById(R.id.dayView);
		if (dayView != null) {
			fragmentManager.beginTransaction().remove(dayView).commit();
		}

		Bundle args = new Bundle();
		args.putSerializable(Shared.DATE_TO_SHOW, date);
		args.putBoolean(DayViewFragment.IS_EMBEDDED, true);

		Fragment newFragment = new DayViewFragment();
		newFragment.setArguments(args);

		fragmentManager.beginTransaction().add(R.id.dayView, newFragment)
				.commit();
	}

	@Override
	public void onMonthViewItemChangedByUser(Calendar date) {
		if (mIsLargeLayout) {
			showAsEmbeddedFragment(date);
		}
	}

	@Override
	public void onMonthViewItemLongClick(Calendar date) {
		if (!mIsLargeLayout) {
			showAsDialogFragment(date);
		}
	}

	@Override
	public void onDayViewItemChangedByUser(Calendar date) {
		mMonthView.setSelectedDate(date);
	}

	private void showDate(Calendar date) {
		mMonthView.setSelectedDate(date);
		if (mIsLargeLayout) {
			showAsEmbeddedFragment(date);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Calendar date = (Calendar) intent
				.getSerializableExtra(Shared.DATE_TO_SHOW);
		if (date != null) {
			showDate(date);
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
			return true;
		case R.id.clearAllData:
			clearAllDataAction();
			return true;
		case R.id.statistic:
			startActivity(new Intent(this, StatisticActivity.class));
			return true;
		case R.id.rate:
			rateAction();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void clearAllDataAction() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.clearAllDataConfirmation)
				.setMessage(R.string.clearAllDataConfirmationMessage)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								clearAllData();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		builder.create().show();
	}

	private void rateAction() {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + getPackageName()));
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.marketNotFound, Toast.LENGTH_SHORT)
					.show();
		}
	}
}