package com.anna.sent.soft.womancyc.fragments;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.adapters.SpinnerItemArrayAdapter;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public class CalendarItemEditorDialogFragment extends DialogFragment {
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

	public interface DialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);

		public void onDialogNeutralClick(DialogFragment dialog);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	DialogListener mListener;

	public CalendarItemEditorDialogFragment() {
		super();
		log("create");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = null;
		if (getResources().getBoolean(R.bool.isLargeLayout)) {
			v = createView();
			log("onCreateView");
		} else {
			v = super.onCreateView(inflater, container, savedInstanceState);
			log("onCreateView returns null");
		}

		return v;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		log("onActivityCreated");
	}

	@Override
	public void onPause() {
		super.onPause();
		log("onPause");
	}

	@Override
	public void onResume() {
		super.onResume();
		log("onResume");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		log("onDestroy");
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		log("onCreateDialog");
		if (getResources().getBoolean(R.bool.isLargeLayout)) {
			return super.onCreateDialog(savedInstanceState);
		} else {
			Calendar dateToShow = (Calendar) getArguments().getSerializable(
					Shared.DATE_TO_SHOW);
			String title = DateUtils.toString(getActivity(), dateToShow);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(createView())
					.setTitle(title)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mListener
											.onDialogPositiveClick(CalendarItemEditorDialogFragment.this);
								}
							})
					.setNeutralButton(getString(R.string.clear),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mListener
											.onDialogNeutralClick(CalendarItemEditorDialogFragment.this);
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mListener
											.onDialogNegativeClick(CalendarItemEditorDialogFragment.this);
								}
							});

			return builder.create();
		}
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
