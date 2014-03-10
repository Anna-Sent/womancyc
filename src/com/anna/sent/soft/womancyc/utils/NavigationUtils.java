package com.anna.sent.soft.womancyc.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

public class NavigationUtils {
	public static void navigateUp(Activity activity, MenuItem item) {
		Intent upIntent = NavUtils.getParentActivityIntent(activity);
		if (NavUtils.shouldUpRecreateTask(activity, upIntent)) {
			TaskStackBuilder.create(activity)
					.addNextIntentWithParentStack(upIntent).startActivities();
		} else {
			NavUtils.navigateUpTo(activity, upIntent);
		}
	}
}