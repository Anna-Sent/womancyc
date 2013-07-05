package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

	private void log(String msg, boolean debug) {
		if (DEBUG && debug) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private DayViewFragment mDayView;
	private boolean mIsLargeLayout;

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof DayViewFragment) {
			mDayView = (DayViewFragment) fragment;
			mDayView.setListener(this);
			log("attach day view");
		}
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		super.setViews(savedInstanceState);
		setContentView(R.layout.activity_day_view);
		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);
	}

	private Calendar mDateToShow = null;

	@Override
	protected void onStart() {
		log("onStart");
		super.onStart();
		if (mDateToShow == null) {
			mDateToShow = Calendar.getInstance();
		}

		setResult();

		if (mIsLargeLayout) {
			finish();
			return;
		}

		mDayView.setSelectedDate(mDateToShow);
	}

	@Override
	public void restoreState(Bundle state) {
		mDateToShow = (Calendar) state.getSerializable(Shared.DATE_TO_SHOW);
	}

	@Override
	public void saveActivityState(Bundle state) {
		log("save " + DateUtils.toString(this, mDayView.getSelectedDate()),
				true);
		state.putSerializable(Shared.DATE_TO_SHOW, mDayView.getSelectedDate());
	}

	@Override
	protected void dataChanged() {
		mDayView.update();
	}

	@Override
	public void onDayViewItemChangedByUser(Calendar date) {
		mDateToShow = date;
		setResult();
		log("put to result " + DateUtils.toString(this, date), false);
	}

	private void setResult() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(Shared.DATE_TO_SHOW, mDateToShow);
		setResult(RESULT_OK, resultIntent);
	}
}
