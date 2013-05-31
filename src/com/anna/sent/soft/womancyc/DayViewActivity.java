package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.anna.sent.soft.womancyc.fragments.DayViewFragment;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.ChildActivity;

public class DayViewActivity extends ChildActivity implements
		DayViewFragment.Listener {
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

		Calendar date = (Calendar) getIntent().getSerializableExtra(
				Shared.DATE_TO_SHOW);

		Bundle args = new Bundle();
		args.putSerializable(Shared.DATE_TO_SHOW, date);

		Fragment newFragment = new DayViewFragment();
		newFragment.setArguments(args);

		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, newFragment).commit();
	}

	@Override
	public void onCalendarItemChanged(Calendar date) {
	}

	@Override
	protected void dataChanged() {
	}
}
