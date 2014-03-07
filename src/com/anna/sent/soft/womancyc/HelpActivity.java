package com.anna.sent.soft.womancyc;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import com.anna.sent.soft.womancyc.adapters.HelpPagerAdapter;
import com.anna.sent.soft.womancyc.base.StateSaverActivity;

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
		super.setViews(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ActionBarHelper().setupActionBar();
		}

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabsAdapter = new HelpPagerAdapter(this, getSupportFragmentManager());
		mViewPager.setAdapter(mTabsAdapter);
		mViewPager.setOffscreenPageLimit(mTabsAdapter.getCount() - 1);
		mViewPager.setCurrentItem(0);
	}

	private class ActionBarHelper {
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		private void setupActionBar() {
			ActionBar actionBar = getActionBar();
			if (actionBar != null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.create(this)
						.addNextIntentWithParentStack(upIntent)
						.startActivities();
			} else {
				NavUtils.navigateUpTo(this, upIntent);
			}

			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}