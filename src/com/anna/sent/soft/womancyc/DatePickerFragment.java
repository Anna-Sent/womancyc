package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import com.anna.sent.soft.womancyc.shared.Shared;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
	OnDateSetListener mListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = (Calendar) getArguments().getSerializable(
				Shared.DATE_TO_SHOW);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(), mListener, year, month, day);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnDateSetListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement "
					+ OnDateSetListener.class.getSimpleName());
		}
	}
}