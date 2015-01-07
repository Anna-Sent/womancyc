package com.anna.sent.soft.womancyc.actions;

import android.content.Intent;
import android.net.Uri;

import com.anna.sent.soft.womancyc.R;

public abstract class MarketActionActivity extends ActionActivity {
	@Override
	protected final Intent getAction() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + getAppName()));
		return intent;
	}

	protected abstract String getAppName();

	@Override
	protected final int getErrorStringResourceId() {
		return R.string.market_app_not_available;
	}
}