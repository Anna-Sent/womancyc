package com.anna.sent.soft.womancyc.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;

import com.anna.sent.soft.womancyc.MainActivity;

public class TaskStackBuilderUtils {
	public static void restartFromSettings(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Intent intent = new Intent(activity, activity.getClass());
			TaskStackBuilder.create(activity)
					.addNextIntentWithParentStack(intent).startActivities();
		} else {
			activity.finish();
			Intent intent = new Intent(activity, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(MainActivity.EXTRA_CONFIGURATION_CHANGED, true);
			activity.startActivity(intent);
		}
	}
}