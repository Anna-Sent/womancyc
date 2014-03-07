package com.anna.sent.soft.womancyc.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public abstract class TitlesPagerAdapter extends FragmentPagerAdapter {
	private String[] mTitles;

	protected abstract int getTitlesArrayResourceId();

	protected TitlesPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mTitles = context.getResources().getStringArray(
				getTitlesArrayResourceId());
	}

	@Override
	public int getCount() {
		return mTitles.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles[position];
	}
}