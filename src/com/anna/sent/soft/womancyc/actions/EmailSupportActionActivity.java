package com.anna.sent.soft.womancyc.actions;

import com.anna.sent.soft.womancyc.R;

public class EmailSupportActionActivity extends EmailActionActivity {
	@Override
	protected String getEmail() {
		return getString(R.string.supportEmail);
	}

	@Override
	protected String getText() {
		return null;
	}
}
