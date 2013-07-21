package com.anna.sent.soft.womancyc.fragments;

import java.util.List;

import org.joda.time.LocalDate;

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
import android.widget.CheckBox;
import android.widget.Spinner;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.adapters.SpinnerItemArrayAdapter;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.DataKeeperClient;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class DayViewFragment extends DialogFragment implements OnClickListener,
		DataKeeperClient, OnItemSelectedListener {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	@SuppressWarnings("unused")
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

	private CalendarListener mListener = null;

	public void setListener(CalendarListener listener) {
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
	private Button buttonCurrentDay, buttonClear, buttonClose, buttonPrevDay,
			buttonNextDay, buttonViewAsList;

	private LocalDate mDateToShow;
	private CalendarData mValue;
	private boolean mIsEmbedded;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = null;
		mIsEmbedded = getResources().getBoolean(R.bool.isLargeLayout);
		if (mIsEmbedded) {
			v = createView(inflater);
			// log("onCreateView");
		} else {
			v = super.onCreateView(inflater, container, savedInstanceState);
			// log("onCreateView returns null");
		}

		return v;
	}

	private View createView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.view_day, null);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// log("onCreateDialog");
		if (mIsEmbedded) {
			return super.onCreateDialog(savedInstanceState);
		} else {
			View v = createView(getActivity().getLayoutInflater());
			spinnerHadMenstruation = (Spinner) v
					.findViewById(R.id.spinnerHadMenstruation);
			spinnerHadSex = (Spinner) v.findViewById(R.id.spinnerSex);
			checkBoxTookPill = (CheckBox) v.findViewById(R.id.checkBoxTookPill);
			textViewNote = (AutoCompleteTextView) v
					.findViewById(R.id.textViewNote);
			buttonClear = (Button) v.findViewById(R.id.buttonClear);
			buttonClose = (Button) v.findViewById(R.id.buttonClose);
			buttonCurrentDay = (Button) v.findViewById(R.id.currentDay);
			buttonPrevDay = (Button) v.findViewById(R.id.prevDay);
			buttonNextDay = (Button) v.findViewById(R.id.nextDay);
			buttonViewAsList = (Button) v.findViewById(R.id.buttonViewAsList);
			setupView();
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(v).setTitle("");
			return builder.create();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// log("onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		if (mIsEmbedded) {
			spinnerHadMenstruation = (Spinner) getActivity().findViewById(
					R.id.spinnerHadMenstruation);
			spinnerHadSex = (Spinner) getActivity().findViewById(
					R.id.spinnerSex);
			checkBoxTookPill = (CheckBox) getActivity().findViewById(
					R.id.checkBoxTookPill);
			textViewNote = (AutoCompleteTextView) getActivity().findViewById(
					R.id.textViewNote);
			buttonClear = (Button) getActivity().findViewById(R.id.buttonClear);
			buttonClose = (Button) getActivity().findViewById(R.id.buttonClose);
			buttonCurrentDay = (Button) getActivity().findViewById(
					R.id.currentDay);
			buttonPrevDay = (Button) getActivity().findViewById(R.id.prevDay);
			buttonNextDay = (Button) getActivity().findViewById(R.id.nextDay);
			buttonViewAsList = (Button) getActivity().findViewById(
					R.id.buttonViewAsList);
			setupView();
		}
	}

	private void setupView() {
		int drawablesId = R.array.menstruationDrawables;
		fillSpinner(R.array.menstruationTypes, drawablesId,
				spinnerHadMenstruation);

		drawablesId = ThemeUtils.getThemeId(getActivity()) == ThemeUtils.DARK_THEME ? R.array.sexDrawablesDark
				: R.array.sexDrawablesLight;
		fillSpinner(R.array.sexTypes, drawablesId, spinnerHadSex);

		checkBoxTookPill.setOnClickListener(this);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, mDataKeeper.getNotes());
		textViewNote.setAdapter(adapter);

		buttonCurrentDay.setOnClickListener(this);
		buttonPrevDay.setOnClickListener(this);
		buttonNextDay.setOnClickListener(this);

		buttonClear.setOnClickListener(this);

		buttonViewAsList.setOnClickListener(this);

		buttonClose.setVisibility(mIsEmbedded ? View.GONE : View.VISIBLE);
		buttonClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		if (getArguments() != null) {
			mDateToShow = (LocalDate) getArguments().getSerializable(
					Shared.DATE_TO_SHOW);
		}

		if (mDateToShow == null) {
			mDateToShow = LocalDate.now();
		}

		update();
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

	public void setSelectedDate(LocalDate value) {
		tryToSave();
		mDateToShow = value;
		update();
	}

	public void update() {
		// log("update " + toString());
		mValue = mDataKeeper.get(mDateToShow);
		if (mValue == null) {
			mValue = new CalendarData(mDateToShow);
		} else {
			mValue = mValue.clone(); // to update properly
		}

		// log(mValue.toString());
		buttonCurrentDay.setText(mDateToShow.toString());
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
		if (!mValue.getNote().equals(note)) {
			textViewNote.setText(mValue.getNote());
		}
	}

	@Override
	public void onPause() {
		// log("onPause");
		super.onPause();
		tryToSave();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonClear:
			mDataKeeper.delete(mValue);
			break;
		case R.id.currentDay:
			if (mListener != null) {
				mListener.showDatePickerToChangeDate();
			}

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
			tryToSave();
			break;
		}
	}

	private void toPrevDay() {
		if (mListener != null) {
			LocalDate dateToShow = mDateToShow.minusDays(1);
			mListener.navigateToDate(dateToShow);
		}
	}

	private void toNextDay() {
		if (mListener != null) {
			LocalDate dateToShow = mDateToShow.plusDays(1);
			mListener.navigateToDate(dateToShow);
		}
	}

	private void tryToSave() {
		int menstruation = spinnerHadMenstruation.getSelectedItemPosition();
		int sex = spinnerHadSex.getSelectedItemPosition();
		boolean tookPill = checkBoxTookPill.isChecked();
		String note = textViewNote.getText().toString();

		boolean isDataChanged = menstruation != mValue.getMenstruation()
				|| sex != mValue.getSex() || tookPill != mValue.getTookPill()
				|| !mValue.getNote().equals(note);
		if (isDataChanged) {
			mValue.setMenstruation(menstruation);
			mValue.setSex(sex);
			mValue.setTookPill(tookPill);
			mValue.setNote(note);
			// log("data is changed");

			mDataKeeper.insertOrUpdate(mValue);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		tryToSave();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		tryToSave();
	}
}
