package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.adapters.MonthCalendarViewAdapter;
import com.anna.sent.soft.womancyc.fragments.CalendarItemEditorDialogFragment;
import com.anna.sent.soft.womancyc.fragments.CalendarItemEditorDialogFragment.DialogListener;
import com.anna.sent.soft.womancyc.fragments.DatePickerFragment;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.OnSwipeTouchListener;
import com.anna.sent.soft.womancyc.utils.StateSaver;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class MainActivity extends FragmentActivity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener, StateSaver,
		DialogListener, OnDateSetListener {
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

	private Button currentMonth;
	private Button prevMonth;
	private Button nextMonth;
	private GridView calendarView;
	private MonthCalendarViewAdapter adapter;
	private static final String CURRENT_MONTH_TEMPLATE = "MMMM yyyy";
	private boolean mIsLargeLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ThemeUtils.onActivityCreateSetTheme(this);
		super.onCreate(savedInstanceState);

		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);

		setViews(savedInstanceState);

		if (savedInstanceState != null) {
			log("restore 1");
			restoreState(savedInstanceState);
		} else {
			savedInstanceState = getIntent().getExtras();
			if (savedInstanceState != null) {
				log("restore 2");
				restoreState(savedInstanceState);
			}
		}
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		setContentView(R.layout.month_calendar_view);

		adapter = new MonthCalendarViewAdapter(this);

		prevMonth = (Button) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) this.findViewById(R.id.currentMonth);
		currentMonth.setOnClickListener(this);
		currentMonth.setText(DateFormat.format(CURRENT_MONTH_TEMPLATE, adapter
				.getSelectedDate().getTime()));

		nextMonth = (Button) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) this.findViewById(R.id.calendarGridView);
		calendarView.setAdapter(adapter);
		calendarView.setOnItemClickListener(this);
		calendarView.setOnItemLongClickListener(this);
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
	}

	@Override
	public void restoreState(Bundle state) {
		Calendar dateToShow = (Calendar) state
				.getSerializable(Shared.DATE_TO_SHOW);
		log("restore " + DateUtils.toString(this, dateToShow));
		updateMonthCalendar(dateToShow);
	}

	@Override
	public void saveState(Bundle state) {
		log("save " + DateUtils.toString(this, adapter.getSelectedDate()));
		state.putSerializable(Shared.DATE_TO_SHOW, adapter.getSelectedDate());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		log("onSaveInstanceState");
		saveState(outState);
		super.onSaveInstanceState(outState);
	}

	private void updateMonthCalendar(Calendar dateToShow) {
		currentMonth.setText(DateFormat.format(CURRENT_MONTH_TEMPLATE,
				dateToShow.getTime()));
		adapter.setSelectedDate(dateToShow);
	}

	private void toPrevDate() {
		Calendar dateToShow = (Calendar) adapter.getSelectedDate().clone();
		dateToShow.set(Calendar.DAY_OF_MONTH, 1);
		dateToShow.add(Calendar.MONTH, -1);
		updateMonthCalendar(dateToShow);
	}

	private void toNextDate() {
		Calendar dateToShow = (Calendar) adapter.getSelectedDate().clone();
		dateToShow.set(Calendar.DAY_OF_MONTH, 1);
		dateToShow.add(Calendar.MONTH, 1);
		updateMonthCalendar(dateToShow);
	}

	@Override
	public void onClick(View v) {
		if (v == prevMonth) {
			toPrevDate();
		} else if (v == nextMonth) {
			toNextDate();
		} else if (v == currentMonth) {
			Bundle args = new Bundle();
			args.putSerializable(Shared.DATE_TO_SHOW, adapter.getSelectedDate());
			DialogFragment dialog = new DatePickerFragment();
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(),
					DatePickerFragment.class.getSimpleName());
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar dateToShow = Calendar.getInstance();
		dateToShow.set(year, month, day);
		updateMonthCalendar(dateToShow);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Object item = adapter.getItem(position);
		if (item != null) {
			Calendar date = (Calendar) item;

			updateMonthCalendar(date);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		Object item = adapter.getItem(position);
		if (item != null) {
			Calendar date = (Calendar) item;

			String title = DateUtils.toString(this, date);
			Bundle args = new Bundle();
			args.putString(Shared.DATE_TO_SHOW, title);

			showCalendarItemEditor();
		}

		return true;
	}

	public void showCalendarItemEditor() {
		FragmentManager fragmentManager = getSupportFragmentManager();

		Bundle args = new Bundle();
		args.putSerializable(Shared.DATE_TO_SHOW, adapter.getSelectedDate());

		CalendarItemEditorDialogFragment newFragment = new CalendarItemEditorDialogFragment();
		newFragment.setArguments(args);

		if (mIsLargeLayout) {
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.add(android.R.id.content, newFragment)
					.addToBackStack(null).commit();
		} else {
			newFragment.show(fragmentManager,
					CalendarItemEditorDialogFragment.class.getSimpleName());
		}
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		Toast.makeText(this, "positive", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDialogNeutralClick(DialogFragment dialog) {
		Toast.makeText(this, "neutral", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		Toast.makeText(this, "negative", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		switch (ThemeUtils.getThemeId(this)) {
		case ThemeUtils.LIGHT_THEME:
			menu.findItem(R.id.lighttheme).setChecked(true);
			break;
		case ThemeUtils.DARK_THEME:
			menu.findItem(R.id.darktheme).setChecked(true);
			break;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.lighttheme:
			ThemeUtils.changeToTheme(this, ThemeUtils.LIGHT_THEME);
			return true;
		case R.id.darktheme:
			ThemeUtils.changeToTheme(this, ThemeUtils.DARK_THEME);
			return true;
		case R.id.help:
			/*
			 * Intent intent = new Intent(); intent.setClass(this,
			 * HelpActivity.class);
			 * 
			 * MainActivityStateSaver.save(this, intent);
			 * 
			 * startActivity(intent); return true;
			 */
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
