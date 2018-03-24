package com.anna.sent.soft.womancyc.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
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
import com.anna.sent.soft.womancyc.base.DataKeeperClient;
import com.anna.sent.soft.womancyc.base.WcDialogFragment;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.shared.Shared;

import org.joda.time.LocalDate;

import java.util.Date;
import java.util.List;

public class DayViewFragment extends WcDialogFragment implements OnClickListener,
        DataKeeperClient, OnItemSelectedListener {
    private CalendarListener mListener;
    private DataKeeper mDataKeeper;
    private Spinner spinnerHadMenstruation, spinnerHadSex;
    private CheckBox checkBoxTookPill;
    private AutoCompleteTextView textViewNote;
    private Button buttonCurrentDay;
    private Button buttonClear;
    private Button buttonPrevDay;
    private Button buttonNextDay;
    private Button buttonViewAsList;
    private LocalDate mDateToShow;
    private CalendarData mValue;
    private boolean mIsEmbedded;

    public void setListener(CalendarListener listener) {
        mListener = listener;
    }

    @Override
    public void setDataKeeper(DataKeeper dataKeeper) {
        mDataKeeper = dataKeeper;
    }

    @SuppressWarnings("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        mIsEmbedded = getResources().getBoolean(R.bool.isLargeLayout);
        if (mIsEmbedded) {
            v = inflater.inflate(R.layout.view_day_embedded, null);
            log("onCreateView");
        } else {
            v = super.onCreateView(inflater, container, savedInstanceState);
            log("onCreateView returns null");
        }

        return v;
    }

    @SuppressWarnings("InflateParams")
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        log("onCreateDialog");
        if (mIsEmbedded) {
            return super.onCreateDialog(savedInstanceState);
        } else {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View v = inflater.inflate(R.layout.view_day_dialog, null);
            spinnerHadMenstruation = v.findViewById(R.id.spinnerHadMenstruation);
            spinnerHadSex = v.findViewById(R.id.spinnerSex);
            checkBoxTookPill = v.findViewById(R.id.checkBoxTookPill);
            textViewNote = v.findViewById(R.id.textViewNote);
            buttonClear = v.findViewById(R.id.buttonClear);
            Button buttonClose = v.findViewById(R.id.buttonClose);
            buttonClose.setOnClickListener(this);
            buttonCurrentDay = v.findViewById(R.id.currentDay);
            buttonPrevDay = v.findViewById(R.id.prevDay);
            buttonNextDay = v.findViewById(R.id.nextDay);
            buttonViewAsList = v.findViewById(R.id.buttonViewAsList);
            setupView();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(v);
            return builder.create();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        log("onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if (mIsEmbedded) {
            //noinspection ConstantConditions
            spinnerHadMenstruation = getActivity().findViewById(R.id.spinnerHadMenstruation);
            spinnerHadSex = getActivity().findViewById(R.id.spinnerSex);
            checkBoxTookPill = getActivity().findViewById(R.id.checkBoxTookPill);
            textViewNote = getActivity().findViewById(R.id.textViewNote);
            buttonClear = getActivity().findViewById(R.id.buttonClear);
            buttonCurrentDay = getActivity().findViewById(R.id.currentDay);
            buttonPrevDay = getActivity().findViewById(R.id.prevDay);
            buttonNextDay = getActivity().findViewById(R.id.nextDay);
            buttonViewAsList = getActivity().findViewById(R.id.buttonViewAsList);
            setupView();
        }
    }

    private void setupView() {
        int drawablesId = R.array.menstruationDrawables;
        fillSpinner(R.array.menstruationTypes, drawablesId,
                spinnerHadMenstruation);

        drawablesId = settingsTheme.isDefaultTheme() ? R.array.sexDrawablesDark
                : R.array.sexDrawablesLight;
        fillSpinner(R.array.sexTypes, drawablesId, spinnerHadSex);

        checkBoxTookPill.setOnClickListener(this);

        //noinspection ConstantConditions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, R.id.spinnerItemTextView, mDataKeeper.getNotes());
        textViewNote.setAdapter(adapter);

        buttonCurrentDay.setOnClickListener(this);
        buttonPrevDay.setOnClickListener(this);
        buttonNextDay.setOnClickListener(this);
        buttonClear.setOnClickListener(this);
        buttonViewAsList.setOnClickListener(this);

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
        log("update " + toString());
        mValue = mDataKeeper.get(mDateToShow);
        if (mValue == null) {
            mValue = new CalendarData(mDateToShow);
        } else {
            mValue = new CalendarData(mValue); // to update properly
        }

        log(mValue.toString());
        Date date = mDateToShow.toDate();
        String formattedString = DateFormat.getDateFormat(getActivity())
                .format(date);
        buttonCurrentDay.setText(formattedString);
        int menstruation = spinnerHadMenstruation.getSelectedItemPosition();
        if (menstruation != mValue.getMenstruation()) {
            spinnerHadMenstruation.setSelection(mValue.getMenstruation());
        }

        int sex = spinnerHadSex.getSelectedItemPosition();
        if (sex != mValue.getSex()) {
            spinnerHadSex.setSelection(mValue.getSex());
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
        log("onPause");
        super.onPause();
        tryToSave();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonClear:
                mDataKeeper.delete(mValue);
                break;
            case R.id.buttonClose:
                dismiss();
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
                            android.R.string.ok, null);
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());
                    builder.setItems(list.toArray(new String[list.size()]),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    textViewNote.setText(list.get(which));
                                }
                            }).setNegativeButton(android.R.string.cancel, null);
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
            log("data is changed");

            mDataKeeper.insertOrUpdate(mValue);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        tryToSave();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        tryToSave();
    }
}
