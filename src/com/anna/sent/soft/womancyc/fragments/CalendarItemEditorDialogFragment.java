package com.anna.sent.soft.womancyc.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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

	private void log(String msg, boolean debug) {
		if (DEBUG && debug) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private boolean mIsLargeLayout;
	private Spinner spinnerHadMenstruation, spinnerHadSex;
	private AutoCompleteTextView textViewNote;

	private CalendarData mValue;

	public CalendarItemEditorDialogFragment() {
		super();
		log("create", false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = null;
		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);
		if (mIsLargeLayout) {
			v = createView();
			log("onCreateView", false);
		} else {
			v = super.onCreateView(inflater, container, savedInstanceState);
			log("onCreateView returns null", false);
		}

		return v;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		log("onActivityCreated", false);
		super.onActivityCreated(arg0);
	}

	@Override
	public void onPause() {
		log("onPause", false);
		super.onPause();
		if (mIsLargeLayout) {
			onDialogPositiveClick();
		}
	}

	@Override
	public void onResume() {
		log("onResume", false);
		super.onResume();
	}

	@Override
	public void onDestroy() {
		log("onDestroy", false);
		super.onDestroy();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		log("onCreateDialog", false);
		if (mIsLargeLayout) {
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

	private void fillSpinner(int stringsArray, int imagesArray, Spinner spinner) {
		String[] strings = getResources().getStringArray(stringsArray);
		TypedArray ta = getResources().obtainTypedArray(imagesArray);
		Drawable[] drawables = new Drawable[strings.length];
		for (int i = 0; i < strings.length; ++i) {
			if (i == 0) {
				drawables[i] = null;
			} else {
				drawables[i] = ta.getDrawable(i);
			}
		}

		ta.recycle();

		ArrayAdapter<String> adapter = new SpinnerItemArrayAdapter(
				getActivity(), strings, drawables);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(this);
	}

	private View createView() {
		mValue = (CalendarData) getArguments().getSerializable(
				CalendarData.class.getSimpleName());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.calendar_item_editor, null);

		spinnerHadMenstruation = (Spinner) v
				.findViewById(R.id.spinnerHadMenstruation);
		fillSpinner(R.array.menstruationTypes, R.array.menstruationDrawables,
				spinnerHadMenstruation);

		spinnerHadSex = (Spinner) v.findViewById(R.id.spinnerSex);
		fillSpinner(R.array.sexTypes, R.array.sexDrawables, spinnerHadSex);

		textViewNote = (AutoCompleteTextView) v.findViewById(R.id.textViewNote);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, mDataKeeper.getNotes());
		textViewNote.setAdapter(adapter);

		Button clear = (Button) v.findViewById(R.id.buttonClear);
		TextView title = (TextView) v.findViewById(R.id.textViewTitle);
		if (!mIsLargeLayout) {
			clear.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
		} else {
			clear.setOnClickListener(this);
			title.setText(getTitle());
		}

		fillWithData();

		return v;
	}

	private void fillWithData() {
		spinnerHadMenstruation.setSelection((int) mValue.getMenstruation());
		spinnerHadSex.setSelection((int) mValue.getSex());
		textViewNote.setText(mValue.getNote());
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
		onDataChanged();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
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
			mValue.clear();
			fillWithData();
		}
	}

	private void onDialogNegativeClick() {
		if (mDataKeeper != null) {
			mDataKeeper.cancel(mValue);
		}
	}

	private void onDataChanged() {
		if (mIsLargeLayout && mDataKeeper != null) {
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

		printEquality(mValue.getMenstruation(), menstruation);
		printEquality(mValue.getSex(), sex);
		printEquality(mValue.getNote(), note);

		boolean isDataChanged = menstruation != mValue.getMenstruation()
				|| sex != mValue.getSex() || !isEqual(note, mValue.getNote());
		if (isDataChanged) {
			mValue.setMenstruation(menstruation);
			mValue.setSex(sex);
			mValue.setNote(note);
			log("data is changed");
		}

		return isDataChanged;
	}

	private void printEquality(int value1, int value2) {
		if (value1 != value2) {
			log(value1 + " != " + value2);
		}
	}

	private void printEquality(String s1, String s2) {
		if (!isEqual(s1, s2)) {
			log(toString(s1) + " != " + toString(s2));
		}
	}

	private String toString(String s) {
		return s == null ? "null" : "\"" + s + "\"";
	}

	private boolean isEqual(String s1, String s2) {
		if (s1 == null) {
			s1 = "";
		}

		if (s2 == null) {
			s2 = "";
		}

		return s1.equals(s2);
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
