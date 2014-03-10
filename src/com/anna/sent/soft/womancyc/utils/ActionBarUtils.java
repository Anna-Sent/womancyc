package com.anna.sent.soft.womancyc.utils;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;

public class ActionBarUtils {
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setupActionBar(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = activity.getActionBar();
			if (actionBar != null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
		}
	}
}