package com.anna.sent.soft.womancyc.actions;

import com.anna.sent.soft.womancyc.R;

import android.content.Intent;
import android.net.Uri;

public abstract class EmailActionActivity extends ActionActivity {
	@Override
	protected final Intent getAction() {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:" + getEmail()));
		intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
		intent.putExtra(Intent.EXTRA_TEXT, getText());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
	}

	protected abstract String getEmail();

	protected abstract String getText();

	@Override
	protected final int getErrorStringResourceId() {
		return R.string.sendto_app_not_available;
	}
}