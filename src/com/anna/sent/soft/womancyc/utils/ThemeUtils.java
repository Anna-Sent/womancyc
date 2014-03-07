package com.anna.sent.soft.womancyc.utils;

import android.app.Activity;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.shared.Settings;

public class ThemeUtils {
	public final static int DARK_THEME = 0;
	public final static int LIGHT_THEME = 1;

	/**
	 * Set the theme of the activity, according to the configuration.
	 */
	public static void onActivityCreateSetTheme(Activity activity) {
		switch (Settings.getTheme(activity)) {
		case LIGHT_THEME:
			activity.setTheme(R.style.AppThemeLight);
			break;
		case DARK_THEME:
		default:
			activity.setTheme(R.style.AppTheme);
			break;
		}
	}
}