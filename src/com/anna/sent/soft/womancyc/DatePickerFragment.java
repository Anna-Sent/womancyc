package com.anna.sent.soft.womancyc;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.anna.sent.soft.womancyc.shared.Shared;

public class DatePickerFragment extends DialogFragment implements
		DialogInterface.OnClickListener {
	private DatePicker mDatePicker = null;
	private OnDateSetListener mListener = null;

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

		try {
			Field field = DatePickerDialog.class
					.getDeclaredField("mDatePicker");
			field.setAccessible(true);
			mDatePicker = (DatePicker) field.get(dialog);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		return dialog;
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

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_NEUTRAL && mDatePicker != null) {
			mListener.onDateSet(mDatePicker, mDatePicker.getYear(),
					mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
		}
	}
}