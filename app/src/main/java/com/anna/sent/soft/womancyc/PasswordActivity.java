package com.anna.sent.soft.womancyc;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.base.StateSaverActivity;
import com.anna.sent.soft.womancyc.shared.Settings;

public class PasswordActivity extends StateSaverActivity implements
        OnClickListener, OnEditorActionListener {
    private EditText mEditTextPassword;

    @Override
    public void setViews(Bundle savedInstanceState) {
        if (Settings.isApplicationLocked(this)) {
            setTitle(R.string.app_name);
            setContentView(R.layout.activity_password);
            mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
            mEditTextPassword.setOnEditorActionListener(this);
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
        if (v.getId() == R.id.buttonOk) {
            checkPassword();
        }
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            checkPassword();
            return true;
        }

        return false;
    }

    private void checkPassword() {
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
        startActivity(new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
