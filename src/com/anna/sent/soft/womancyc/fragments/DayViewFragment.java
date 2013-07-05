package com.anna.sent.soft.womancyc.fragments;

import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
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

	/**
	 * must be not null! fragment gets it when onAttach() is called by parent
	 * activity
	 */
	private DataKeeper mDataKeeper = null;

	@Override
	public void setDataKeeper(DataKeeper dataKeeper) {
		mDataKeeper = dataKeeper;
	}

	private Spinner spinnerHadMenstruation, spinnerHadSex;
	private CheckBox checkBoxTookPill;
	private AutoCompleteTextView textViewNote;
	private Button currentDay;

	private Calendar mDateToShow;
	private CalendarData mValue;
	private boolean mIsEmbedded;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		log("onCreateView");
		View v = inflater.inflate(R.layout.view_day, null);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		log("onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		spinnerHadMenstruation = (Spinner) getActivity().findViewById(
				R.id.spinnerHadMenstruation);
		int drawablesId = R.array.menstruationDrawables;
		fillSpinner(R.array.menstruationTypes, drawablesId,
				spinnerHadMenstruation);

		spinnerHadSex = (Spinner) getActivity().findViewById(R.id.spinnerSex);
		drawablesId = ThemeUtils.getThemeId(getActivity()) == ThemeUtils.DARK_THEME ? R.array.sexDrawablesDark
				: R.array.sexDrawablesLight;
		fillSpinner(R.array.sexTypes, drawablesId, spinnerHadSex);

		checkBoxTookPill = (CheckBox) getActivity().findViewById(
				R.id.checkBoxTookPill);
		checkBoxTookPill.setOnClickListener(this);

		textViewNote = (AutoCompleteTextView) getActivity().findViewById(
				R.id.textViewNote);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, mDataKeeper.getNotes());
		textViewNote.setAdapter(adapter);

		currentDay = (Button) getActivity().findViewById(R.id.currentDay);
		currentDay.setOnClickListener(this);
		Button prevDay = (Button) getActivity().findViewById(R.id.prevDay);
		prevDay.setOnClickListener(this);
		Button nextDay = (Button) getActivity().findViewById(R.id.nextDay);
		nextDay.setOnClickListener(this);

		Button clear = (Button) getActivity().findViewById(R.id.buttonClear);
		clear.setOnClickListener(this);

		Button viewAsList = (Button) getActivity().findViewById(
				R.id.buttonViewAsList);
		viewAsList.setOnClickListener(this);

		mIsEmbedded = getResources().getBoolean(R.bool.isLargeLayout);
		Button close = (Button) getActivity().findViewById(R.id.buttonClose);
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

		Calendar value = null;
		if (getArguments() != null) {
			value = (Calendar) getArguments().getSerializable(
					Shared.DATE_TO_SHOW);
		}

		if (value == null) {
			value = Calendar.getInstance();
		}

		setSelectedDate(value);
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

	public Calendar getSelectedDate() {
		return mDateToShow;
	}

	public void setSelectedDate(Calendar value) {
		mDateToShow = value;
		update();
	}

	public void update() {
		log("update " + toString(), true);
		if (getActivity() == null)
			log("activity is null", true);
		mValue = mDataKeeper.get(mDateToShow);
		if (mValue == null) {
			mValue = new CalendarData(mDateToShow);
		}

		currentDay.setText(DateUtils.toString(getActivity(), mDateToShow));
		int menstruation = spinnerHadMenstruation.getSelectedItemPosition();
		if (menstruation != mValue.getMenstruation()) {
			spinnerHadMenstruation.setSelection((int) mValue.getMenstruation());
		}

		int sex = spinnerHadSex.getSelectedItemPosition();
		if (sex != mValue.getSex()) {
			spinnerHadSex.setSelection((int) mValue.getSex());
		}

		boolean tookPill = checkBoxTookPill.isChecked();
		if (tookPill != mValue.getTookPill()) {
			checkBoxTookPill.setChecked(mValue.getTookPill());
		}

		String note = textViewNote.getText().toString();
		if (!isEqual(note, mValue.getNote())) {
			textViewNote.setText(mValue.getNote());
		}
	}

	@Override
	public void onPause() {
		log("onPause");
		super.onPause();
		tryToSave();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonClear:
			clear();
			break;
		case R.id.currentDay:
			Bundle args = new Bundle();
			args.putSerializable(Shared.DATE_TO_SHOW, mDateToShow);
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
		case R.id.buttonViewAsList:
			final List<String> list = mDataKeeper.getNotes();
			if (list.size() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setMessage(R.string.thereIsNoData).setPositiveButton(
						android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				builder.create().show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setItems(list.toArray(new String[] {}),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								textViewNote.setText(list.get(which));
							}
						}).setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				builder.create().show();
			}

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
		if (!DateUtils.datesAreEqual(dateToShow, mDateToShow)) {
			if (mListener != null) {
				mListener.onDayViewItemChangedByUser(dateToShow);
			}

			tryToSave();
			setSelectedDate(dateToShow);
		}
	}

	private void toPrevDay() {
		Calendar dateToShow = (Calendar) mDateToShow.clone();
		dateToShow.add(Calendar.DAY_OF_MONTH, -1);
		if (mListener != null) {
			mListener.onDayViewItemChangedByUser(dateToShow);
		}

		tryToSave();
		setSelectedDate(dateToShow);
	}

	private void toNextDay() {
		Calendar dateToShow = (Calendar) mDateToShow.clone();
		dateToShow.add(Calendar.DAY_OF_MONTH, 1);
		if (mListener != null) {
			mListener.onDayViewItemChangedByUser(dateToShow);
		}

		tryToSave();
		setSelectedDate(dateToShow);
	}

	private void clear() {
		mDataKeeper.delete(mValue);
		update();
	}

	private void tryToSave() {
		int menstruation = spinnerHadMenstruation.getSelectedItemPosition();
		int sex = spinnerHadSex.getSelectedItemPosition();
		boolean tookPill = checkBoxTookPill.isChecked();
		String note = textViewNote.getText().toString();

		boolean isDataChanged = menstruation != mValue.getMenstruation()
				|| sex != mValue.getSex() || tookPill != mValue.getTookPill()
				|| !isEqual(note, mValue.getNote());
		if (isDataChanged) {
			mValue.setMenstruation(menstruation);
			mValue.setSex(sex);
			mValue.setTookPill(tookPill);
			mValue.setNote(note);
			log("data is changed");

			mDataKeeper.insertOrUpdate(mValue);
		}
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
