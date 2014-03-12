package com.anna.sent.soft.womancyc.utils;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;

public class ActionBarUtils {
	// hack for Android 1.6
	public static void setupActionBar(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBarHelper.setupActionBar(activity);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static class ActionBarHelper {
		private static void setupActionBar(Activity activity) {
			ActionBar actionBar = activity.getActionBar();
			if (actionBar != null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
		}
	}
}