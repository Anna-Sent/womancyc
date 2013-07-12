package com.anna.sent.soft.womancyc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.anna.sent.soft.womancyc.shared.Settings;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;
import com.anna.sent.soft.womancyc.utils.UserEmailFetcher;

public class SendEmailActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getClass().equals(SendEmailActivity.class)) {
			Intent intent = new Intent(
					this,
					ThemeUtils.DARK_THEME == ThemeUtils.getThemeId(this) ? SendEmailActivityDark.class
							: SendEmailActivityLight.class);
			startActivity(intent);
			finish();
		} else {
			setup();
		}
	}

	protected void setup() {
		setContentView(R.layout.activity_send_email);

		Button buttonSend = (Button) findViewById(R.id.buttonSend);
		Button buttonClose = (Button) findViewById(R.id.buttonClose);
		final EditText textTo = (EditText) findViewById(R.id.editTextTo);
		textTo.setText(UserEmailFetcher.getEmail(this));
		final EditText textSubject = (EditText) findViewById(R.id.editTextSubject);
		final EditText textMessage = (EditText) findViewById(R.id.editTextMessage);
		textMessage.setText(Settings.getPassword(this));

		buttonSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String to = textTo.getText().toString();
				String subject = textSubject.getText().toString();
				String message = textMessage.getText().toString();
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.setData(Uri.parse("mailto:" + to));
				intent.putExtra(Intent.EXTRA_SUBJECT, subject);
				intent.putExtra(Intent.EXTRA_TEXT, message);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});

		buttonClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}