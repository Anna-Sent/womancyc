package com.anna.sent.soft.womancyc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.anna.sent.soft.womancyc.adapters.SpinnerItemArrayAdapter;
import com.anna.sent.soft.womancyc.shared.Shared;

public class CalendarItemEditorDialogFragment extends DialogFragment {
	public interface DialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	DialogListener mListener;

	/*
	 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
	 * container, Bundle savedInstanceState) { View v =
	 * inflater.inflate(R.layout.calendar_item_editor, container, false); /*
	 * 
	 * /* return v; }
	 * 
	 * /*
	 * 
	 * @Override public Dialog onCreateDialog(Bundle savedInstanceState) { //
	 * The only reason you might override this method when using //
	 * onCreateView() is // to modify any dialog characteristics. For example,
	 * the dialog // includes a // title by default, but your custom layout
	 * might not need it. So // here you can // remove the dialog title, but you
	 * must call the superclass to get // the Dialog. Dialog dialog =
	 * super.onCreateDialog(savedInstanceState);
	 * dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); return dialog; } }
	 */

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(createView())
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

	private void fillSpinner(int stringArrayResourceId, Spinner spinner) {
		String[] data = getResources().getStringArray(stringArrayResourceId);
		ArrayAdapter<String> adapter = new SpinnerItemArrayAdapter(
				getActivity(), data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
		spinner.setSelection(0);
	}

	private View createView() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.calendar_item_editor, null);

		fillSpinner(R.array.menstruationTypes,
				(Spinner) v.findViewById(R.id.spinnerMenstruation));
		fillSpinner(R.array.sexTypes, (Spinner) v.findViewById(R.id.spinnerSex));

		return v;
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
