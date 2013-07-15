package com.anna.sent.soft.womancyc.adapters;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.fragments.TabHelpFragmentFactory;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class HelpPagerAdapter extends TitlesPagerAdapter {
	public HelpPagerAdapter(Context context, FragmentManager fm) {
		super(context, fm);
	}

	@Override
	protected int getTitlesArrayResourceId() {
		return R.array.helpTitles;
	}

	@Override
	public Fragment getItem(int position) {
		return TabHelpFragmentFactory.newInstance(position);
	}
}
