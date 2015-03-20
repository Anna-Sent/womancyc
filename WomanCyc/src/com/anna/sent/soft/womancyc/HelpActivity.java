package com.anna.sent.soft.womancyc;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.anna.sent.soft.utils.ActionBarUtils;
import com.anna.sent.soft.utils.NavigationUtils;
import com.anna.sent.soft.womancyc.adapters.HelpPagerAdapter;
import com.anna.sent.soft.womancyc.base.StateSaverActivity;

public final class HelpActivity extends StateSaverActivity {
	private ViewPager mViewPager;
	private HelpPagerAdapter mTabsAdapter;

	@Override
	public void setViews(Bundle savedInstanceState) {
		setTitle(R.string.help);
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
			NavigationUtils.navigateUp(this);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}