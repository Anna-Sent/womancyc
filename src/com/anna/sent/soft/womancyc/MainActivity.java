package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.CalendarItemEditorDialogFragment.DialogListener;
import com.anna.sent.soft.womancyc.adapters.MonthCalendarViewAdapter;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.OnSwipeTouchListener;
import com.anna.sent.soft.womancyc.utils.StateSaver;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class MainActivity extends FragmentActivity implements OnClickListener,
		OnItemClickListener, StateSaver, DialogListener, OnDateSetListener {
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
	private Calendar mDateToShow = null;
	private static final String CURRENT_MONTH_TEMPLATE = "MMMM yyyy";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ThemeUtils.onActivityCreateSetTheme(this);
		super.onCreate(savedInstanceState);

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

		if (mDateToShow == null) {
			toCurrentDate();
		}
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		setContentView(R.layout.simple_calendar_view);

		prevMonth = (Button) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) this.findViewById(R.id.currentMonth);
		currentMonth.setOnClickListener(this);

		nextMonth = (Button) this.findViewById(R.id.nextMonth);
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
	}

	@Override
	public void restoreState(Bundle state) {
		mDateToShow = (Calendar) state.getSerializable(Shared.DATE_TO_SHOW);
		log("restore " + DateUtils.toString(this, mDateToShow));
		updateMonthCalendar();
	}

	@Override
	public void saveState(Bundle state) {
		log("save " + DateUtils.toString(this, mDateToShow));
		state.putSerializable(Shared.DATE_TO_SHOW, mDateToShow);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		log("onSaveInstanceState");
		saveState(outState);
		super.onSaveInstanceState(outState);
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
			Bundle args = new Bundle();
			args.putSerializable(Shared.DATE_TO_SHOW, mDateToShow);
			DialogFragment dialog = new DatePickerFragment();
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(),
					DatePickerFragment.class.getSimpleName());
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		mDateToShow.set(year, month, day);
		updateMonthCalendar();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Object item = arg0.getAdapter().getItem(arg2);
		if (item != null) {
			Calendar calendar = (Calendar) item;

			String title = DateUtils.toString(this, calendar);
			Bundle args = new Bundle();
			args.putString(Shared.DATE_TO_SHOW, title);

			DialogFragment dialog = new CalendarItemEditorDialogFragment();
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(),
					CalendarItemEditorDialogFragment.class.getSimpleName());
		}
	}

	/*
	 * public void showDialog() { FragmentManager fragmentManager =
	 * getSupportFragmentManager(); CustomDialogFragment newFragment = new
	 * CustomDialogFragment();
	 * 
	 * if (mIsLargeLayout) { // The device is using a large layout, so show the
	 * fragment as a dialog newFragment.show(fragmentManager, "dialog"); } else
	 * { // The device is smaller, so show the fragment fullscreen
	 * FragmentTransaction transaction = fragmentManager.beginTransaction(); //
	 * For a little polish, specify a transition animation
	 * transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //
	 * To make it fullscreen, use the 'content' root view as the container //
	 * for the fragment, which is always the root view for the activity
	 * transaction.add(android.R.id.content, newFragment)
	 * .addToBackStack(null).commit(); } }
	 */

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		Toast.makeText(this, "positive", Toast.LENGTH_SHORT).show();
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
