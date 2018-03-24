package com.anna.sent.soft.womancyc.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.adapters.MonthViewAdapter;
import com.anna.sent.soft.womancyc.base.DataKeeperClient;
import com.anna.sent.soft.womancyc.base.WcFragment;
import com.anna.sent.soft.womancyc.database.DataKeeper;

import org.joda.time.LocalDate;

import java.util.Locale;

public class MonthViewFragment extends WcFragment
        implements MonthViewAdapter.Listener, OnClickListener, DataKeeperClient {
    private CalendarListener mListener;
    private DataKeeper mDataKeeper;
    private Button currentMonth;
    private MonthViewAdapter adapter;

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
        return inflater.inflate(R.layout.view_month, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log("onActivityCreated");
        adapter = new MonthViewAdapter(getActivity(), mDataKeeper, this);

        //noinspection ConstantConditions
        Button prevMonth = getActivity().findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = getActivity().findViewById(R.id.currentMonth);
        currentMonth.setOnClickListener(this);

        Button nextMonth = getActivity().findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        GridView calendarView = getActivity().findViewById(R.id.calendarGridView);
        calendarView.setAdapter(adapter);
    }

    public LocalDate getSelectedDate() {
        return adapter.getSelectedDate();
    }

    public void setSelectedDate(LocalDate date) {
        log("set selected date to " + date);
        adapter.setSelectedDate(date);

        int month = date.getMonthOfYear();
        int year = date.getYear();
        String[] monthNames = getResources().getStringArray(R.array.MonthNames);

        currentMonth.setText(String.format(Locale.US, "%s %d", monthNames[month - 1], year));
    }

    @Override
    public void onItemClick() {
        if (mListener != null) {
            mListener.updateDetailedView();
        }
    }

    @Override
    public void onItemLongClick() {
        if (mListener != null) {
            mListener.showDetailedView();
        }
    }

    public void update() {
        adapter.update();
    }

    private void toPrevMonth() {
        if (mListener != null) {
            LocalDate dateToShow = new LocalDate(
                    adapter.getYear(), adapter.getMonth(), 1).minusMonths(1);
            mListener.navigateToDate(dateToShow);
        }
    }

    private void toNextMonth() {
        if (mListener != null) {
            LocalDate dateToShow = new LocalDate(
                    adapter.getYear(), adapter.getMonth(), 1).plusMonths(1);
            mListener.navigateToDate(dateToShow);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prevMonth:
                toPrevMonth();
                break;
            case R.id.nextMonth:
                toNextMonth();
                break;
            case R.id.currentMonth:
                if (mListener != null) {
                    mListener.showDatePickerToChangeDate();
                }

                break;
        }
    }
}
