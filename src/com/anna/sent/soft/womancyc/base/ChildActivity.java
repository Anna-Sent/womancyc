package com.anna.sent.soft.womancyc.base;

import android.os.Bundle;
import android.view.MenuItem;

import com.anna.sent.soft.utils.ActionBarUtils;
import com.anna.sent.soft.utils.NavigationUtils;

public abstract class ChildActivity extends DataKeeperActivity {
	@Override
	public void setViews(Bundle savedInstanceState) {
		ActionBarUtils.setupActionBar(this);
		super.setViews(savedInstanceState);
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