package com.anna.sent.soft.womancyc.fragments;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.base.WcDialogFragment;
import com.anna.sent.soft.womancyc.shared.Shared;

import org.joda.time.LocalDate;

public class DatePickerDialogFragment extends WcDialogFragment
        implements DialogInterface.OnClickListener {
    private OnDateSetListener mListener;

    public void setOnDateSetListener(OnDateSetListener listener) {
        mListener = listener;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions") final LocalDate c =
                (LocalDate) getArguments().getSerializable(Shared.DATE_TO_SHOW);
        @SuppressWarnings("ConstantConditions") DatePickerDialog dialog =
                new DatePickerDialog(getActivity(), mListener,
                        c.getYear(), c.getMonthOfYear() - 1, c.getDayOfMonth());
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.today), this);

        dialog.getDatePicker().setCalendarViewShown(false);

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            LocalDate today = LocalDate.now();
            if (mListener != null) {
                mListener.onDateSet(null,
                        today.getYear(), today.getMonthOfYear() - 1, today.getDayOfMonth());
            }
        }
    }
}
