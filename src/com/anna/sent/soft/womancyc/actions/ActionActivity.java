package com.anna.sent.soft.womancyc.actions;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public abstract class ActionActivity extends Activity {
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Intent intent = getAction();
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			} else {
				Toast.makeText(this, getErrorStringResourceId(),
						Toast.LENGTH_LONG).show();
			}
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, getErrorStringResourceId(), Toast.LENGTH_LONG)
					.show();
		}

		finish();
	}

	protected abstract Intent getAction();

	protected abstract int getErrorStringResourceId();
}