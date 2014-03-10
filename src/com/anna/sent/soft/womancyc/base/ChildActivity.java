package com.anna.sent.soft.womancyc.base;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.anna.sent.soft.womancyc.utils.ActionBarUtils;
import com.anna.sent.soft.womancyc.utils.NavigationUtils;

public abstract class ChildActivity extends DataKeeperActivity {
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

	@Override
	public void setViews(Bundle savedInstanceState) {
		new ActionBarUtils().setupActionBar(this);
		super.setViews(savedInstanceState);
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