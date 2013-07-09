package com.anna.sent.soft.womancyc.fragments;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.anna.sent.soft.womancyc.R;

public class PasswordEditPreference extends DialogPreference {

	public PasswordEditPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.dialog_password_edit);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);

		setDialogIcon(null);
	}

}
