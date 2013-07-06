package com.anna.sent.soft.womancyc.utils;

import com.anna.sent.soft.womancyc.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import com.anna.sent.soft.womancyc.shared.Settings;

public class ThemeUtils {
	public final static int DARK_THEME = 0;
	public final static int LIGHT_THEME = 1;

	public static int getThemeId(Context context) {
		return Settings.getTheme(context);
	}

	/**
	 * Set the theme of the Activity, and restart it by creating a new Activity
	 * of the same type.
	 */
	public static void applyChanges(Activity activity) {
		Intent intent = new Intent(activity, activity.getClass());
		TaskStackBuilder.create(activity).addNextIntentWithParentStack(intent)
				.startActivities();
	}

	/**
	 * Set the theme of the activity, according to the configuration.
	 */
	public static void onActivityCreateSetTheme(Activity activity) {
		switch (getThemeId(activity)) {
		case LIGHT_THEME:
			activity.setTheme(R.style.AppThemeLight);
			break;
		case DARK_THEME:
		default:
			activity.setTheme(R.style.AppTheme);
			break;
		}
	}

	/**
	 * Set the theme of the dialog-style activity, according to the
	 * configuration.
	 */
	public static void onDialogStyleActivityCreateSetTheme(Activity activity) {
		switch (getThemeId(activity)) {
		case LIGHT_THEME:
			activity.setTheme(R.style.DialogThemeLight);
			break;
		case DARK_THEME:
		default:
			activity.setTheme(R.style.DialogTheme);
			break;
		}
	}
}
