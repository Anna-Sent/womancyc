package com.anna.sent.soft.womancyc.fragments;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.shared.Shared;

public class DatePickerDialogFragment extends DialogFragment implements
		DialogInterface.OnClickListener {
	private OnDateSetListener mListener = null;

	public void setOnDateSetListener(OnDateSetListener listener) {
		mListener = listener;
	}

	public DatePickerDialogFragment() {
		super();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = (Calendar) getArguments().getSerializable(
				Shared.DATE_TO_SHOW);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dialog = new DatePickerDialog(getActivity(),
				mListener, year, month, day);
		dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
				getString(R.string.today), this);
		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_NEUTRAL) {
			Calendar today = Calendar.getInstance();
			int year = today.get(Calendar.YEAR);
			int month = today.get(Calendar.MONTH);
			int day = today.get(Calendar.DAY_OF_MONTH);
			if (mListener != null) {
				mListener.onDateSet(null, year, month, day);
			}
		}
	}
}