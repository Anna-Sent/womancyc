package com.anna.sent.soft.womancyc.shared;

import android.content.Context;
import android.content.SharedPreferences;

public class Shared {
	private static final String SETTINGS_FILE = "childbirthdatesettings";

	public static SharedPreferences getSettings(Context context) {
		return context.getApplicationContext().getSharedPreferences(
				SETTINGS_FILE, Context.MODE_PRIVATE);
	}
}
