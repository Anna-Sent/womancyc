package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private MonthCalendarViewAdapter adapter;
	private Calendar mDateToShow;
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

		adapter = new MonthCalendarViewAdapter(this);

		calendarView = (GridView) this.findViewById(R.id.calendar);
		calendarView.setAdapter(adapter);
		calendarView.setOnItemClickListener(this);
		calendarView.setOnTouchListener(new OnSwipeTouchListener(this) {
			@Override
			public boolean onSwipeRight() {
				toPrevDate();
				return true;
			}

			@Override
			public boolean onSwipeLeft() {
				toNextDate();
				return true;
			}
		});

		toCurrentDate();
	}

	private void updateMonthCalendar() {
		currentMonth.setText(DateFormat.format(CURRENT_MONTH_TEMPLATE,
				mDateToShow.getTime()));
		int month = mDateToShow.get(Calendar.MONTH);
		int year = mDateToShow.get(Calendar.YEAR);
		adapter.updateMonthCalendar(month, year);
	}

	private void toPrevDate() {
		mDateToShow.add(Calendar.MONTH, -1);
		updateMonthCalendar();
	}

	private void toCurrentDate() {
		mDateToShow = Calendar.getInstance();
		mDateToShow.set(Calendar.DAY_OF_MONTH, 1);
		updateMonthCalendar();
	}

	private void toNextDate() {
		mDateToShow.add(Calendar.MONTH, 1);
		updateMonthCalendar();
	}

	@Override
	public void onClick(View v) {
		if (v == prevMonth) {
			toPrevDate();
		} else if (v == nextMonth) {
			toNextDate();
		} else if (v == currentMonth) {
			toCurrentDate();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Object item = arg0.getAdapter().getItem(arg2);
		if (item != null) {
			Calendar calendar = (Calendar) item;
			String title = DateFormat.getDateFormat(this).format(
					calendar.getTime());
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(title)
					.setMessage("message")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			builder.create().show();
		}
	}
}
