package com.anna.sent.soft.womancyc.utils;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.shared.Shared;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

public class ThemeUtils {
	private static final String EXTRA_GUI_THEME_ID = "com.anna.sent.soft.childbirthdate.themeid";

	public final static int DARK_THEME = 0;
	public final static int LIGHT_THEME = 1;
	public final static int DEFAULT_THEME = DARK_THEME;

	public static int getThemeId(Context context) {
		SharedPreferences settings = Shared.getSettings(context);
		return settings.getInt(EXTRA_GUI_THEME_ID, DEFAULT_THEME);
	}

	/**
	 * Set the theme of the Activity, and restart it by creating a new Activity
	 * of the same type.
	 */
	public static void changeToTheme(Activity activity, int themeId) {
		if (getThemeId(activity) != themeId) {
			SharedPreferences settings = Shared.getSettings(activity);
			Editor editor = settings.edit();
			editor.putInt(EXTRA_GUI_THEME_ID, themeId);
			editor.commit();

			Bundle state = new Bundle();
			StateSaver stateSaverActivity = (StateSaver) activity;
			stateSaverActivity.saveState(state);

			activity.finish();

			Intent intent = new Intent(activity, activity.getClass());
			intent.putExtras(state);
			activity.startActivity(intent);
		}
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
