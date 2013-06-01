package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.anna.sent.soft.womancyc.fragments.DayViewFragment;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.DialogActivity;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public class DayViewActivity extends DialogActivity implements
		DayViewFragment.Listener {
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

	private boolean mIsLargeLayout;
	private Calendar mDateToShow;
	private DayViewFragment mDayView;

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof DayViewFragment) {
			mDayView = (DayViewFragment) fragment;
			mDayView.setListener(this);
		}
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		super.setViews(savedInstanceState);

		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);

		if (savedInstanceState == null) {
			mDateToShow = (Calendar) getIntent().getSerializableExtra(
					Shared.DATE_TO_SHOW);
			log("got from intent " + DateUtils.toString(this, mDateToShow));
		} else {
			myRestoreState(savedInstanceState);
		}

		setResult();

		if (mIsLargeLayout) {
			finish();
			return;
		}

		Bundle args = new Bundle();
		args.putSerializable(Shared.DATE_TO_SHOW, mDateToShow);

		Fragment newFragment = new DayViewFragment();
		newFragment.setArguments(args);
		log("set args " + DateUtils.toString(this, mDateToShow));

		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, newFragment).commit();
	}

	@Override
	protected void beforeOnSaveInstanceState() {
		FragmentManager fm = getSupportFragmentManager();

		Fragment dayView = fm.findFragmentById(android.R.id.content);
		if (dayView != null) {
			fm.beginTransaction().remove(dayView).commit();
		}
	}

	public void myRestoreState(Bundle state) {
		mDateToShow = (Calendar) state.getSerializable(Shared.DATE_TO_SHOW);
		log("restore " + DateUtils.toString(this, mDateToShow));
	}

	@Override
	protected void saveActivityState(Bundle state) {
		state.putSerializable(Shared.DATE_TO_SHOW, mDateToShow);
		log("save " + DateUtils.toString(this, mDateToShow));
	}

	@Override
	public void onCalendarItemChanged(Calendar date) {
		mDateToShow = date;
		setResult();
		log("put to result " + DateUtils.toString(this, date));
	}

	private void setResult() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(Shared.DATE_TO_SHOW, mDateToShow);
		setResult(RESULT_OK, resultIntent);
	}
}
