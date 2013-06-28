package com.anna.sent.soft.womancyc.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.adapters.SpinnerItemArrayAdapter;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.DataKeeperClient;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class DayViewFragment extends Fragment implements OnClickListener,
		DataKeeperClient, OnDateSetListener, OnItemSelectedListener {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	@SuppressWarnings("unused")
	private void log(String msg, boolean debug) {
		if (DEBUG && debug) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	public interface Listener {
		public void onDayViewItemChangedByUser(Calendar date);
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

	private Spinner spinnerHadMenstruation, spinnerHadSex;
	private CheckBox checkBoxTookPill;
	private AutoCompleteTextView textViewNote;
	private Button currentDay;

	private CalendarData mValue = null;
	private boolean mIsEmbedded;

	public static final String IS_EMBEDDED = "isembedded";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		log("onCreateView");
		View v = createView();
		return v;
	}

	@Override
	public void onPause() {
		log("onPause");
		super.onPause();
		tryToSave();
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
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.view_day, null);

		spinnerHadMenstruation = (Spinner) v
				.findViewById(R.id.spinnerHadMenstruation);
		int drawablesId = R.array.menstruationDrawables;
		fillSpinner(R.array.menstruationTypes, drawablesId,
				spinnerHadMenstruation);

		spinnerHadSex = (Spinner) v.findViewById(R.id.spinnerSex);
		drawablesId = ThemeUtils.getThemeId(getActivity()) == ThemeUtils.DARK_THEME ? R.array.sexDrawablesDark
				: R.array.sexDrawablesLight;
		fillSpinner(R.array.sexTypes, drawablesId, spinnerHadSex);

		checkBoxTookPill = (CheckBox) v.findViewById(R.id.checkBoxTookPill);
		checkBoxTookPill.setOnClickListener(this);

		textViewNote = (AutoCompleteTextView) v.findViewById(R.id.textViewNote);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1,
				mDataKeeper == null ? new ArrayList<String>() : mDataKeeper
						.getNotes());
		textViewNote.setAdapter(adapter);

		Button clear = (Button) v.findViewById(R.id.buttonClear);
		currentDay = (Button) v.findViewById(R.id.currentDay);
		currentDay.setOnClickListener(this);
		Button prevDay = (Button) v.findViewById(R.id.prevDay);
		prevDay.setOnClickListener(this);
		Button nextDay = (Button) v.findViewById(R.id.nextDay);
		nextDay.setOnClickListener(this);
		clear.setOnClickListener(this);

		Calendar date = (Calendar) getArguments().getSerializable(
				Shared.DATE_TO_SHOW);
		setDate(date);

		mIsEmbedded = getArguments().getBoolean(IS_EMBEDDED, false);
		Button close = (Button) v.findViewById(R.id.buttonClose);
		close.setVisibility(mIsEmbedded ? View.GONE : View.VISIBLE);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});

		if (mIsEmbedded) {
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			clear.setLayoutParams(params);
		}

		return v;
	}

	private void fillWithData() {
		currentDay.setText(DateUtils.toString(getActivity(), mValue.getDate()));
		spinnerHadMenstruation.setSelection((int) mValue.getMenstruation());
		spinnerHadSex.setSelection((int) mValue.getSex());
		checkBoxTookPill.setChecked(mValue.getTookPill());
		textViewNote.setText(mValue.getNote());
	}

	private void setDate(Calendar date) {
		if (mValue != null) {
			tryToSave();
		}

		mValue = mDataKeeper == null ? null : mDataKeeper.get(date);
		if (mValue == null) {
			mValue = new CalendarData(date);
		}

		fillWithData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonClear:
			clear();
			break;
		case R.id.currentDay:
			Bundle args = new Bundle();
			args.putSerializable(Shared.DATE_TO_SHOW, mValue.getDate());
			DatePickerDialogFragment dialog = new DatePickerDialogFragment();
			dialog.setArguments(args);
			dialog.setOnDateSetListener(this);
			dialog.show(getFragmentManager(), dialog.getClass().getSimpleName());
			break;
		case R.id.nextDay:
			toNextDay();
			break;
		case R.id.prevDay:
			toPrevDay();
			break;
		case R.id.checkBoxTookPill:
			if (mIsEmbedded) {
				tryToSave();
			}

			break;
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar dateToShow = Calendar.getInstance();
		dateToShow.set(year, month, day);
		if (!DateUtils.datesAreEqual(dateToShow, mValue.getDate())) {
			if (mListener != null) {
				mListener.onDayViewItemChangedByUser(dateToShow);
			}

			setDate(dateToShow);
		}
	}

	private void toPrevDay() {
		Calendar dateToShow = (Calendar) mValue.getDate().clone();
		dateToShow.add(Calendar.DAY_OF_MONTH, -1);
		if (mListener != null) {
			mListener.onDayViewItemChangedByUser(dateToShow);
		}

		setDate(dateToShow);
	}

	private void toNextDay() {
		Calendar dateToShow = (Calendar) mValue.getDate().clone();
		dateToShow.add(Calendar.DAY_OF_MONTH, 1);
		if (mListener != null) {
			mListener.onDayViewItemChangedByUser(dateToShow);
		}

		setDate(dateToShow);
	}

	private void clear() {
		if (mDataKeeper != null) {
			mDataKeeper.delete(mValue);
			setDate(mValue.getDate());
		}
	}

	private void tryToSave() {
		if (mDataKeeper != null) {
			boolean isDataChanged = updateDataIfNeeded();
			if (isDataChanged) {
				mDataKeeper.insertOrUpdate(mValue);
			}
		}
	}

	private boolean updateDataIfNeeded() {
		int menstruation = spinnerHadMenstruation.getSelectedItemPosition();
		int sex = spinnerHadSex.getSelectedItemPosition();
		boolean tookPill = checkBoxTookPill.isChecked();
		String note = textViewNote.getText().toString();

		printEquality(mValue.getMenstruation(), menstruation);
		printEquality(mValue.getSex(), sex);
		printEquality(mValue.getNote(), note);

		boolean isDataChanged = menstruation != mValue.getMenstruation()
				|| sex != mValue.getSex() || tookPill != mValue.getTookPill()
				|| !isEqual(note, mValue.getNote());
		if (isDataChanged) {
			mValue.setMenstruation(menstruation);
			mValue.setSex(sex);
			mValue.setTookPill(tookPill);
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (mIsEmbedded) {
			tryToSave();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		if (mIsEmbedded) {
			tryToSave();
		}
	}
}
