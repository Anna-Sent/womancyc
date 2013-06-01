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

	private boolean mIsLargeLayout;
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

		if (mResultIntent == null) {
			setResult(RESULT_CANCELED);
		} else {
			setResult(RESULT_OK, mResultIntent);
		}

		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);

		if (mIsLargeLayout) {
			finish();
			return;
		}

		if (savedInstanceState == null) {
			Calendar date = (Calendar) getIntent().getSerializableExtra(
					Shared.DATE_TO_SHOW);

			Bundle args = new Bundle();
			args.putSerializable(Shared.DATE_TO_SHOW, date);

			Fragment newFragment = new DayViewFragment();
			newFragment.setArguments(args);

			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, newFragment).commit();
		}
	}

	private static Intent mResultIntent = null;

	@Override
	public void onCalendarItemChanged(Calendar date) {
		mResultIntent = new Intent();
		mResultIntent.putExtra(Shared.DATE_TO_SHOW, date);
		setResult(RESULT_OK, mResultIntent);
		log("put to result " + DateUtils.toString(this, date));
	}
}
