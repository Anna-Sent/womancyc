package com.anna.sent.soft.womancyc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.base.StateSaverActivity;
import com.anna.sent.soft.womancyc.shared.Settings;

public class PasswordActivity extends StateSaverActivity implements
		OnClickListener {
	private EditText mEditTextPassword;

	@Override
	public void setViews(Bundle savedInstanceState) {
		super.setViews(savedInstanceState);
		if (Settings.isApplicationLocked(this)) {
			setContentView(R.layout.activity_password);
			mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
			Button buttonOk = (Button) findViewById(R.id.buttonOk);
			buttonOk.setOnClickListener(this);
		} else {
			startProtectedActivity();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mEditTextPassword.setText("");
	}

	@Override
	public void onClick(View v) {
		String password = mEditTextPassword.getText().toString();
		if (password.equals(Settings.getPassword(this))) {
			startProtectedActivity();
		} else {
			Toast.makeText(this, getString(R.string.incorrectPassword),
					Toast.LENGTH_LONG).show();
		}
	}

	private void startProtectedActivity() {
		finish();
		startActivity(new Intent(this, MainActivity.class));
	}
}
