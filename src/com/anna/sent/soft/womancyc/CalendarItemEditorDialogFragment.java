package com.anna.sent.soft.womancyc;

import com.anna.sent.soft.womancyc.shared.Shared;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

public class CalendarItemEditorDialogFragment extends DialogFragment {
	public interface DialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	DialogListener mListener;
/*
	public class CustomDialogFragment extends DialogFragment {
		/**
		 * The system calls this to get the DialogFragment's layout, regardless
		 * of whether it's being displayed as a dialog or an embedded fragment.
		 *//*
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Inflate the layout to use as dialog or embedded fragment
			return inflater.inflate(R.layout.purchase_items, container, false);
		}*/

		/** The system calls this only when creating the layout in a dialog. */
		/*@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// The only reason you might override this method when using
			// onCreateView() is
			// to modify any dialog characteristics. For example, the dialog
			// includes a
			// title by default, but your custom layout might not need it. So
			// here you can
			// remove the dialog title, but you must call the superclass to get
			// the Dialog.
			Dialog dialog = super.onCreateDialog(savedInstanceState);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			return dialog;
		}
	}*/

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.calendar_item_editor, null))
				.setTitle(getArguments().getString(Shared.DATE_TO_SHOW))
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mListener
										.onDialogPositiveClick(CalendarItemEditorDialogFragment.this);
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mListener
										.onDialogNegativeClick(CalendarItemEditorDialogFragment.this);
							}
						});
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (DialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement " + DialogListener.class.getSimpleName());
		}
	}
}
