package com.anna.sent.soft.womancyc.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.anna.sent.soft.womancyc.R;

public class PasswordPreference extends DialogPreference {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private String mOldPassword, mNewPassword, mConfirmedPassword;
	private EditText mEditTextOldPassword, mEditTextNewPassword,
			mEditTextConfirmedPassword;

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
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		persistString(restore ? getPersistedString("") : (String) defaultValue);
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		TextView textViewOldPassword = (TextView) view
				.findViewById(R.id.textViewOldPassword);
		mEditTextOldPassword = (EditText) view
				.findViewById(R.id.editTextOldPassword);
		if (mNewPassword == null || mNewPassword.equals("")) {
			textViewOldPassword.setVisibility(View.GONE);
			mEditTextOldPassword.setVisibility(View.GONE);
		} else {
			textViewOldPassword.setVisibility(View.VISIBLE);
			textViewOldPassword.setText(R.string.enterOldPassword);
			mEditTextOldPassword.setVisibility(View.VISIBLE);
			mEditTextOldPassword.setText(mOldPassword);
		}

		TextView textViewNewPassword = (TextView) view
				.findViewById(R.id.textViewNewPassword);
		textViewNewPassword.setText(R.string.enterNewPassword);

		TextView textViewConfirmPassword = (TextView) view
				.findViewById(R.id.textViewConfirmPassword);
		textViewConfirmPassword.setText(R.string.confirmPassword);

		mEditTextNewPassword = (EditText) view
				.findViewById(R.id.editTextNewPassword);
		mEditTextNewPassword.setText(mNewPassword);

		mEditTextConfirmedPassword = (EditText) view
				.findViewById(R.id.editTextConfirmedPassword);
		mEditTextConfirmedPassword.setText(mConfirmedPassword);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			// check old pass
			// check confirm pass
			String password = mEditTextNewPassword.getText().toString();
			persistString(password);
			mEditTextNewPassword = null;
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
		if (mEditTextNewPassword != null) {
			myState.newPassword = mEditTextNewPassword.getText().toString();
			log("save " + myState.newPassword);
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
		if (mEditTextNewPassword != null) {
			mEditTextNewPassword.setText(myState.newPassword);
			log("restore " + myState.newPassword);
		}
	}

	private static class SavedState extends BaseSavedState {
		public String newPassword, oldPassword, confirmedPassword;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			newPassword = source.readString();
			oldPassword = source.readString();
			confirmedPassword = source.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(newPassword);
			dest.writeString(oldPassword);
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
