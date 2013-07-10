package com.anna.sent.soft.womancyc.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.R;

public class PasswordPreference extends DialogPreference {
	private static final String TAG = "moo";
	private static final boolean DEBUG = true;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private String mPassword;
	private EditText mEditTextPassword, mEditTextConfirmedPassword;

	public PasswordPreference(Context context) {
		this(context, null);
	}

	public PasswordPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		setDialogLayoutResource(R.layout.dialog_password);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		setDialogIcon(null);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		if (restorePersistedValue) {
			mPassword = getPersistedString("");
		} else {
			mPassword = (String) defaultValue;
			persistString(mPassword);
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		mEditTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
		mEditTextPassword.setText(mPassword);
		mEditTextConfirmedPassword = (EditText) view
				.findViewById(R.id.editTextConfirmedPassword);

		Button clearPass = (Button) view.findViewById(R.id.buttonClearPassword);
		clearPass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditTextPassword.setText("");
			}
		});

		Button clearConfirmedPass = (Button) view
				.findViewById(R.id.buttonClearConfirmedPassword);
		clearConfirmedPass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditTextConfirmedPassword.setText("");
			}
		});
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			String confirmedPassword = mEditTextConfirmedPassword.getText()
					.toString();
			confirmedPassword = confirmedPassword == null ? ""
					: confirmedPassword;
			String newPassword = mEditTextPassword.getText().toString();
			newPassword = newPassword == null ? "" : newPassword;
			String message;
			if (confirmedPassword.equals(newPassword)) {
				if (newPassword.equals("")) {
					message = getContext().getResources().getString(
							R.string.passwordIsNotSet);
				} else {
					message = getContext().getResources().getString(
							R.string.passwordIsSet);
				}
			} else {
				message = getContext().getResources().getString(
						R.string.confirmationUnsuccess);
				newPassword = "";
			}

			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
			mPassword = newPassword;
			persistString(mPassword);

			mEditTextPassword = null;
			mEditTextConfirmedPassword = null;
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		log("save");
		final Parcelable superState = super.onSaveInstanceState();

		/*
		 * if (isPersistent()) { return superState; }
		 */

		final SavedState myState = new SavedState(superState);
		if (mEditTextPassword != null && mEditTextConfirmedPassword != null) {
			myState.password = mEditTextPassword.getText().toString();
			log("save password \"" + myState.password + "\"");
			myState.confirmedPassword = mEditTextConfirmedPassword.getText()
					.toString();
			log("save confirmed password \"" + myState.confirmedPassword + "\"");
		}

		return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		log("restore");
		if (state == null || !state.getClass().equals(SavedState.class)) {
			log("restore " + state == null ? "null" : state.getClass()
					.getName());
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		if (mEditTextPassword != null && mEditTextConfirmedPassword != null) {
			mEditTextPassword.setText(myState.password);
			log("restore password \"" + myState.password + "\"");
			mEditTextConfirmedPassword.setText(myState.confirmedPassword);
			log("restore confirmed password \"" + myState.confirmedPassword
					+ "\"");
		}
	}

	private static class SavedState extends BaseSavedState {
		public String password, confirmedPassword;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			password = source.readString();
			confirmedPassword = source.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(password);
			dest.writeString(confirmedPassword);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
