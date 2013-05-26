package com.anna.sent.soft.womancyc.fragments;

import java.util.Calendar;

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

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.adapters.SpinnerItemArrayAdapter;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.superclasses.DataKeeperClient;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public class DayViewFragment extends DialogFragment implements OnClickListener,
		OnItemSelectedListener, DataKeeperClient {
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

	public interface Listener {
		public void onCalendarItemChanged(Calendar date);
	}

	private Listener mListener = null;

	public void setListener(Listener listener) {
		mListener = listener;
	}

	private DataKeeper mDataKeeper = null;

	@Override
	public void setDataKeeper(DataKeeper dataKeeper) {
		mDataKeeper = dataKeeper;
	}

	private boolean mIsLargeLayout;
	private Spinner spinnerHadMenstruation, spinnerHadSex;
	private AutoCompleteTextView textViewNote;
	private Button currentDay;

	private CalendarData mValue;

	public DayViewFragment() {
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
	public void onResume() {
		log("onResume", false);
		super.onResume();
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
					.setTitle("")
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
		View v = inflater.inflate(R.layout.view_day, null);

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
		currentDay = (Button) v.findViewById(R.id.currentDay);
		Button prevDay = (Button) v.findViewById(R.id.prevDay);
		prevDay.setOnClickListener(this);
		Button nextDay = (Button) v.findViewById(R.id.nextDay);
		nextDay.setOnClickListener(this);
		if (!mIsLargeLayout) {
			clear.setVisibility(View.GONE);
		} else {
			clear.setOnClickListener(this);
		}

		fillWithData();

		return v;
	}

	private void fillWithData() {
		currentDay.setText(DateUtils.toString(getActivity(), mValue.getDate()));
		spinnerHadMenstruation.setSelection((int) mValue.getMenstruation());
		spinnerHadSex.setSelection((int) mValue.getSex());
		textViewNote.setText(mValue.getNote());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonClear:
			onDialogNeutralClick();
			break;
		case R.id.currentDay:
			/*
			 * Bundle args = new Bundle();
			 * args.putSerializable(Shared.DATE_TO_SHOW,
			 * mMonthView.getSelectedDate()); DatePickerDialogFragment dialog =
			 * new DatePickerDialogFragment(); dialog.setArguments(args);
			 * dialog.setOnDateSetListener(this);
			 * dialog.show(getSupportFragmentManager(), dialog.getClass()
			 * .getSimpleName());
			 */
			break;
		case R.id.nextDay:
			toNextDay();
			break;
		case R.id.prevDay:
			toPrevDay();
			break;
		}
	}

	private void toPrevDay() {
		Calendar dateToShow = (Calendar) mValue.getDate().clone();
		dateToShow.add(Calendar.DAY_OF_MONTH, -1);
		if (mListener != null) {
			mListener.onCalendarItemChanged(dateToShow);
		}

		if (!mIsLargeLayout) {
			mValue = mDataKeeper.get(dateToShow);
			if (mValue == null) {
				mValue = new CalendarData(dateToShow);
			}

			fillWithData();
		}
	}

	private void toNextDay() {
		Calendar dateToShow = (Calendar) mValue.getDate().clone();
		dateToShow.add(Calendar.DAY_OF_MONTH, 1);
		if (mListener != null) {
			mListener.onCalendarItemChanged(dateToShow);
		}

		if (!mIsLargeLayout) {
			mValue = mDataKeeper.get(dateToShow);
			if (mValue == null) {
				mValue = new CalendarData(dateToShow);
			}

			fillWithData();
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
}
