package com.anna.sent.soft.womancyc.fragments;

import org.joda.time.LocalDate;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final LocalDate c = (LocalDate) getArguments().getSerializable(
				Shared.DATE_TO_SHOW);
		DatePickerDialog dialog = new DatePickerDialog(getActivity(),
				mListener, c.getYear(), c.getMonthOfYear() - 1,
				c.getDayOfMonth());
		dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
				getString(R.string.today), this);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			dialog.getDatePicker().setCalendarViewShown(false);
		}

		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_NEUTRAL) {
			LocalDate today = LocalDate.now();
			if (mListener != null) {
				mListener.onDateSet(null, today.getYear(),
						today.getMonthOfYear() - 1, today.getDayOfMonth());
			}
		}
	}
}