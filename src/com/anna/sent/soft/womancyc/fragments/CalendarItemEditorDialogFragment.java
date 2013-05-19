package com.anna.sent.soft.womancyc.fragments;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.adapters.SpinnerItemArrayAdapter;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public class CalendarItemEditorDialogFragment extends DialogFragment implements
		OnClickListener, OnItemSelectedListener {
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

		public void onDataChanged();
	}

	private DialogListener mListener = null;
	private boolean mIsDialog;
	private Spinner spinnerHadMenstruation, spinnerHadSex;

	public CalendarItemEditorDialogFragment() {
		super();
		log("create");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = null;
		if (getResources().getBoolean(R.bool.isLargeLayout)) {
			mIsDialog = false;
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
		if (!mIsDialog) {
			onDialogPositiveClick();
		}
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
			mIsDialog = true;
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(createView())
					.setTitle(getTitle())
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									onDialogPositiveClick();
								}
							})
					.setNeutralButton(getString(R.string.clear),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									onDialogNeutralClick();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									onDialogNegativeClick();
								}
							});

			return builder.create();
		}
	}

	private void fillSpinner(int stringArrayResourceId, int[] images,
			Spinner spinner) {
		String[] data = getResources().getStringArray(stringArrayResourceId);
		ArrayAdapter<String> adapter = new SpinnerItemArrayAdapter(
				getActivity(), data, images);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
		spinner.setSelection(0);

		spinner.setOnItemSelectedListener(this);
	}

	private View createView() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.calendar_item_editor, null);

		int[] images = new int[] { 0, R.drawable.menstruation,
				R.drawable.one_drop, R.drawable.two_drops,
				R.drawable.three_drops };
		spinnerHadMenstruation = (Spinner) v
				.findViewById(R.id.spinnerHadMenstruation);
		fillSpinner(R.array.menstruationTypes, images, spinnerHadMenstruation);

		spinnerHadSex = (Spinner) v.findViewById(R.id.spinnerSex);
		images = new int[] { 0, R.drawable.unprotected_sex,
				R.drawable.protected_sex };
		fillSpinner(R.array.sexTypes, images, spinnerHadSex);

		Button clear = (Button) v.findViewById(R.id.buttonClear);
		TextView title = (TextView) v.findViewById(R.id.textViewTitle);
		if (mIsDialog) {
			// remove title text view and clear button
			clear.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
		} else {
			clear.setOnClickListener(this);
			title.setText(getTitle());
		}

		return v;
	}

	private String getTitle() {
		Calendar dateToShow = (Calendar) getArguments().getSerializable(
				Shared.DATE_TO_SHOW);
		return DateUtils.toString(getActivity(), dateToShow);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonClear) {
			onDialogNeutralClick();
		}
	}

	public void setDialogListener(DialogListener listener) {
		mListener = listener;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Toast.makeText(getActivity(), "onItemSelected", Toast.LENGTH_SHORT)
				.show();
		onDataChanged();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		Toast.makeText(getActivity(), "onNothingSelected", Toast.LENGTH_SHORT)
				.show();
		onDataChanged();
	}

	private void onDialogPositiveClick() {
		if (mListener != null) {
			mListener.onDialogPositiveClick(this);
		}
	}

	private void onDialogNeutralClick() {
		if (mListener != null) {
			mListener.onDialogNeutralClick(this);
		}
	}

	private void onDialogNegativeClick() {
		if (mListener != null) {
			mListener.onDialogNegativeClick(this);
		}
	}

	private void onDataChanged() {
		boolean isDataChanged = true;
		if (isDataChanged && mListener != null) {
			mListener.onDataChanged();
		}
	}
}
