package com.anna.sent.soft.womancyc;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.DatePicker;

import com.anna.sent.soft.ad.AdUtils;
import com.anna.sent.soft.womancyc.base.OptionsActivity;
import com.anna.sent.soft.womancyc.fragments.CalendarListener;
import com.anna.sent.soft.womancyc.fragments.DatePickerDialogFragment;
import com.anna.sent.soft.womancyc.fragments.DayViewFragment;
import com.anna.sent.soft.womancyc.fragments.MonthViewFragment;
import com.anna.sent.soft.womancyc.shared.Settings;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.google.android.gms.ads.AdView;

import org.joda.time.LocalDate;

public class MainActivity extends OptionsActivity implements CalendarListener, OnDateSetListener {
    private final static String TAG_DAY_VIEW = "day_view_dialog";
    private final static String TAG_DATE_PICKER = "date_picker_dialog";

    private MonthViewFragment mMonthView;
    private boolean mIsLargeLayout;
    private FragmentManager mFragmentManager;
    private LocalDate mDateToShow;
    private AdView mAdView;

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof MonthViewFragment) {
            mMonthView = (MonthViewFragment) fragment;
            mMonthView.setListener(this);
            log("attach month view");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);

        mAdView = AdUtils.setupAd(this, R.id.adView, R.string.adUnitId, true);

        mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);
        Settings.isBlocked(this, false);
        mFragmentManager = getSupportFragmentManager();

        mDateToShow = savedInstanceState == null
                ? null
                : (LocalDate) savedInstanceState.getSerializable(Shared.DATE_TO_SHOW);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDateToShow == null) {
            mDateToShow = LocalDate.now();
        }

        log("date is " + mDateToShow);
        mMonthView.setSelectedDate(mDateToShow);
        if (mIsLargeLayout) {
            showAsEmbeddedFragment();
        }
    }

    public void onStop() {
        mDateToShow = mMonthView.getSelectedDate();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        log("save " + mMonthView.getSelectedDate().toString());
        outState.putSerializable(Shared.DATE_TO_SHOW, mMonthView.getSelectedDate());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    @Override
    protected void dataChanged() {
        log("data changed");
        mMonthView.update();
        DayViewFragment dayView = getDayView();
        if (dayView != null) {
            dayView.update();
        }
    }

    private void showAsDialogFragment() {
        DialogFragment dayView = createDayView();
        dayView.show(mFragmentManager, TAG_DAY_VIEW);
    }

    private void showAsEmbeddedFragment() {
        Fragment dayView = mFragmentManager.findFragmentById(R.id.dayView);
        if (dayView != null) {
            mFragmentManager.beginTransaction().remove(dayView).commit();
        }

        dayView = createDayView();
        mFragmentManager.beginTransaction().add(R.id.dayView, dayView).commit();
    }

    private DayViewFragment createDayView() {
        Bundle args = new Bundle();
        args.putSerializable(Shared.DATE_TO_SHOW, mMonthView.getSelectedDate());
        DayViewFragment newFragment = new DayViewFragment();
        newFragment.setArguments(args);
        newFragment.setListener(this);
        return newFragment;
    }

    @Override
    public void showDatePickerToChangeDate() {
        Bundle args = new Bundle();
        args.putSerializable(Shared.DATE_TO_SHOW, mMonthView.getSelectedDate());
        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.setArguments(args);
        dialog.setOnDateSetListener(this);
        dialog.show(mFragmentManager, TAG_DATE_PICKER);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        LocalDate dateToShow = new LocalDate(year, month + 1, day);
        navigateToDate(dateToShow);
    }

    @Override
    public void navigateToDate(LocalDate date) {
        if (!date.isEqual(mMonthView.getSelectedDate())) {
            log("navigate to date");
            mMonthView.setSelectedDate(date);
            setDayViewToDate();
        }
    }

    @Override
    public void showDetailedView() {
        if (!mIsLargeLayout) {
            showAsDialogFragment();
        }
    }

    @Override
    public void updateDetailedView() {
        if (mIsLargeLayout) {
            setDayViewToDate();
        }
    }

    private void setDayViewToDate() {
        DayViewFragment dayView = getDayView();
        if (dayView != null) {
            dayView.setSelectedDate(mMonthView.getSelectedDate());
        }
    }

    private DayViewFragment getDayView() {
        DayViewFragment dayView = (DayViewFragment) mFragmentManager.findFragmentByTag(TAG_DAY_VIEW);
        if (dayView == null) {
            dayView = (DayViewFragment) mFragmentManager.findFragmentById(R.id.dayView);
        }

        return dayView;
    }
}
