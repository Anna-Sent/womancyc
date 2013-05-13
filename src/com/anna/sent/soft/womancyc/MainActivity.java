package com.anna.sent.soft.womancyc;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener,
		MonthCalendarViewAdapter.OnClickCalendarItemListener {
	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private MonthCalendarViewAdapter adapter;
	private Calendar mCurrentDate;
	private static final String CURRENT_MONTH_TEMPLATE = "MMMM yyyy";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_calendar_view);

		prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) this.findViewById(R.id.currentMonth);
		currentMonth.setOnClickListener(this);

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) this.findViewById(R.id.calendar);

		adapter = new MonthCalendarViewAdapter(this);
		adapter.setOnClickCalendarItemListener(this);
		calendarView.setAdapter(adapter);

		mCurrentDate = Calendar.getInstance();
		mCurrentDate.set(Calendar.DAY_OF_MONTH, 1);
		int month = mCurrentDate.get(Calendar.MONTH);
		int year = mCurrentDate.get(Calendar.YEAR);
		setToDate(month, year);
	}

	private void setToDate(int month, int year) {
		currentMonth.setText(DateFormat.format(CURRENT_MONTH_TEMPLATE,
				mCurrentDate.getTime()));
		adapter.updateMonthCalendar(month, year);
	}

	@Override
	public void onClick(View v) {
		if (v == prevMonth) {
			mCurrentDate.add(Calendar.MONTH, -1);
			int month = mCurrentDate.get(Calendar.MONTH);
			int year = mCurrentDate.get(Calendar.YEAR);
			setToDate(month, year);
		} else if (v == nextMonth) {
			mCurrentDate.add(Calendar.MONTH, 1);
			int month = mCurrentDate.get(Calendar.MONTH);
			int year = mCurrentDate.get(Calendar.YEAR);
			setToDate(month, year);
		} else if (v == currentMonth) {
			mCurrentDate = Calendar.getInstance(Locale.getDefault());
			int month = mCurrentDate.get(Calendar.MONTH);
			int year = mCurrentDate.get(Calendar.YEAR);
			setToDate(month, year);
		}
	}

	@Override
	public void onClickCalendarItem(Calendar calendar) {
		String title = DateFormat.getDateFormat(this)
				.format(calendar.getTime());
		Log.d("moo", title);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title)
				.setMessage("message")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		builder.create().show();
	}
}
