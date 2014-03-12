package com.anna.sent.soft.womancyc;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import com.anna.sent.soft.womancyc.adapters.HelpPagerAdapter;
import com.anna.sent.soft.womancyc.base.StateSaverActivity;
import com.anna.sent.soft.womancyc.utils.ActionBarUtils;
import com.anna.sent.soft.womancyc.utils.NavigationUtils;

public final class HelpActivity extends StateSaverActivity {
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

	private ViewPager mViewPager;
	private HelpPagerAdapter mTabsAdapter;

	@Override
	public void setViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_help);
		ActionBarUtils.setupActionBar(this);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabsAdapter = new HelpPagerAdapter(this, getSupportFragmentManager());
		mViewPager.setAdapter(mTabsAdapter);
		mViewPager.setOffscreenPageLimit(mTabsAdapter.getCount() - 1);
		mViewPager.setCurrentItem(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavigationUtils.navigateUp(this, item);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}