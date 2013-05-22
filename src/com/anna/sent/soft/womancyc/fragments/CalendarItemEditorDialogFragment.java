package com.anna.sent.soft.womancyc.fragments;

import android.app.Activity;
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
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.data.DataKeeper;
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

	private boolean mIsDialog;
	private Spinner spinnerHadMenstruation, spinnerHadSex;
	private TextView textViewNote;

	private CalendarData mValue;

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
			mIsDialog = true;
			v = super.onCreateView(inflater, container, savedInstanceState);
			log("onCreateView returns null");
		}

		return v;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		log("onActivityCreated");
		super.onActivityCreated(arg0);
	}

	@Override
	public void onPause() {
		log("onPause");
		super.onPause();
		if (!mIsDialog) {
			onDialogPositiveClick();
		}
	}

	@Override
	public void onResume() {
		log("onResume");
		super.onResume();
	}

	@Override
	public void onDestroy() {
		log("onDestroy");
		super.onDestroy();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		log("onCreateDialog");
		if (getResources().getBoolean(R.bool.isLargeLayout)) {
			return super.onCreateDialog(savedInstanceState);
		} else {
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

		spinner.setOnItemSelectedListener(this);
	}

	private View createView() {
		mValue = (CalendarData) getArguments().getSerializable(
				CalendarData.class.getSimpleName());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.calendar_item_editor, null);

		int[] images = new int[] { 0, R.drawable.menstruation,
				R.drawable.one_drop, R.drawable.two_drops,
				R.drawable.three_drops };
		spinnerHadMenstruation = (Spinner) v
				.findViewById(R.id.spinnerHadMenstruation);
		fillSpinner(R.array.menstruationTypes, images, spinnerHadMenstruation);
		spinnerHadMenstruation.setSelection((int) mValue.getMenstruation());

		spinnerHadSex = (Spinner) v.findViewById(R.id.spinnerSex);
		images = new int[] { 0, R.drawable.unprotected_sex,
				R.drawable.protected_sex };
		fillSpinner(R.array.sexTypes, images, spinnerHadSex);
		spinnerHadSex.setSelection((int) mValue.getSex());

		textViewNote = (TextView) v.findViewById(R.id.textViewNote);
		textViewNote.setText(mValue.getNote());

		Button clear = (Button) v.findViewById(R.id.buttonClear);
		TextView title = (TextView) v.findViewById(R.id.textViewTitle);
		if (mIsDialog) {
			clear.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
		} else {
			clear.setOnClickListener(this);
			title.setText(getTitle());
		}

		return v;
	}

	private String getTitle() {
		CalendarData value = (CalendarData) getArguments().getSerializable(
				CalendarData.class.getSimpleName());
		return DateUtils.toString(getActivity(), value.getDate());
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonClear) {
			onDialogNeutralClick();
		}
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
		if (mDataKeeper != null) {
			boolean isDataChanged = updateDataIfNeeded();
			if (isDataChanged) {
				mDataKeeper.insertOrUpdate(mValue);
			}
		}
	}

	private void onDialogNeutralClick() {
		if (mDataKeeper != null) {
			mDataKeeper.delete(mValue);
		}
	}

	private void onDialogNegativeClick() {
		if (mDataKeeper != null) {
			mDataKeeper.cancel(mValue);
		}
	}

	private void onDataChanged() {
		if (!mIsDialog && mDataKeeper != null) {
			boolean isDataChanged = updateDataIfNeeded();
			if (isDataChanged) {
				mDataKeeper.insertOrUpdate(mValue);
			}
		}
	}

	private boolean updateDataIfNeeded() {
		int menstruation = spinnerHadMenstruation.getSelectedItemPosition();
		int sex = spinnerHadSex.getSelectedItemPosition();
		String note = textViewNote.getText().toString();
		boolean isDataChanged = menstruation != mValue.getMenstruation()
				|| sex != mValue.getSex() || isEqual(note, mValue.getNote());
		if (isDataChanged) {
			mValue.setMenstruation(menstruation);
			mValue.setSex(sex);
			mValue.setNote(note);
		}

		return isDataChanged;
	}

	private boolean isEqual(String s1, String s2) {
		if ((s1 == null && s2 == "") || (s1 == "" && s2 == null)) {
			return true;
		} else {
			return s1 == s2;
		}
	}

	private DataKeeper mDataKeeper = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof DataKeeper) {
			mDataKeeper = (DataKeeper) activity;
		}
	}
}
