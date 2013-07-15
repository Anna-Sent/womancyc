package com.anna.sent.soft.womancyc;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.fragments.DayViewFragment;
import com.anna.sent.soft.womancyc.fragments.MonthViewFragment;
import com.anna.sent.soft.womancyc.shared.Settings;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.DataKeeperActivity;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class MainActivity extends DataKeeperActivity implements
		MonthViewFragment.Listener, DayViewFragment.Listener {
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

	private MonthViewFragment mMonthView;
	private DayViewFragment mDayView;
	private boolean mIsLargeLayout;

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof MonthViewFragment) {
			mMonthView = (MonthViewFragment) fragment;
			mMonthView.setListener(this);
			log("attach month view");
		}

		if (fragment instanceof DayViewFragment) {
			mDayView = (DayViewFragment) fragment;
			mDayView.setListener(this);
			log("attach day view");
		}
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		super.setViews(savedInstanceState);
		setContentView(R.layout.activity_main);
		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);
		Settings.isBlocked(this, false);
	}

	private Calendar mDateToShow = null;

	@Override
	public void onStart() {
		log("onStart");
		super.onStart();
		if (mDateToShow == null) {
			mDateToShow = Calendar.getInstance();
		}

		showDate(mDateToShow);
	}

	public void onStop() {
		mDateToShow = mMonthView.getSelectedDate();
		super.onStop();
	}

	@Override
	public void restoreState(Bundle state) {
		mDateToShow = (Calendar) state.getSerializable(Shared.DATE_TO_SHOW);
	}

	@Override
	public void saveActivityState(Bundle state) {
		log("save " + DateUtils.toString(this, mMonthView.getSelectedDate()));
		state.putSerializable(Shared.DATE_TO_SHOW, mMonthView.getSelectedDate());
	}

	@Override
	public void beforeOnSaveInstanceState() {
		FragmentManager fm = getSupportFragmentManager();

		Fragment dayView = fm.findFragmentById(R.id.dayView);
		if (dayView != null) {
			fm.beginTransaction().remove(dayView).commit();
		}
	}

	@Override
	protected void dataChanged() {
		mMonthView.update();
		if (mIsLargeLayout) {
			mDayView.update();
		}
	}

	private final static int REQUEST_DATE = 1;

	private void showAsDialogFragment(Calendar date) {
		Intent intent = new Intent(
				this,
				ThemeUtils.DARK_THEME == ThemeUtils.getThemeId(this) ? DayViewActivityDark.class
						: DayViewActivityLight.class);
		intent.putExtra(Shared.DATE_TO_SHOW, date);
		startActivityForResult(intent, REQUEST_DATE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_DATE && resultCode == Activity.RESULT_OK) {
			Calendar date = (Calendar) data
					.getSerializableExtra(Shared.DATE_TO_SHOW);
			log("got from result " + DateUtils.toString(this, date));
			showDate(date);
		}
	}

	private void showAsEmbeddedFragment(Calendar date) {
		FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment dayView = fragmentManager.findFragmentById(R.id.dayView);
		if (dayView != null) {
			fragmentManager.beginTransaction().remove(dayView).commit();
		}

		Bundle args = new Bundle();
		args.putSerializable(Shared.DATE_TO_SHOW, date);

		DayViewFragment newFragment = new DayViewFragment();
		newFragment.setArguments(args);

		fragmentManager.beginTransaction().add(R.id.dayView, newFragment)
				.commit();
	}

	@Override
	public void onMonthViewItemChangedByUser(Calendar date) {
		if (mIsLargeLayout) {
			showAsEmbeddedFragment(date);
		}
	}

	@Override
	public void onMonthViewItemLongClick(Calendar date) {
		if (!mIsLargeLayout) {
			showAsDialogFragment(date);
		}
	}

	@Override
	public void onDayViewItemChangedByUser(Calendar date) {
		mMonthView.setSelectedDate(date);
	}

	private void showDate(Calendar date) {
		mMonthView.setSelectedDate(date);
		if (mIsLargeLayout) {
			showAsEmbeddedFragment(date);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.lockAndExit).setVisible(
				Settings.isPasswordSet(this)
						&& !Settings.lockAutomatically(this));
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.statistic:
			startActivity(new Intent(this, StatisticActivity.class));
			return true;
		case R.id.backupData:
			backupAction();
			return true;
		case R.id.restoreData:
			restoreAction();
			return true;
		case R.id.clearAllData:
			clearAllDataAction();
			return true;
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.help:
			startActivity(new Intent(this, HelpActivity.class));
			return true;
		case R.id.lockAndExit:
			Settings.isBlocked(this, true);
			finish();
			return true;
		case R.id.test25:
			Calendar today = Calendar.getInstance();
			Calendar date = (Calendar) today.clone();
			date.add(Calendar.YEAR, -25);
			int index = 1;
			while (DateUtils.beforeOrEqual(date, today)) {
				log(index + " " + DateUtils.toString(date));
				if (1 <= index && index <= 7) {
					CalendarData value = new CalendarData(date);
					value.setMenstruation(1);
					insertOrUpdate(value);
				}

				++index;
				if (index == 29) {
					index = 1;
				}

				date.add(Calendar.DAY_OF_MONTH, 1);
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void clearAllDataAction() {
		if (getDataKeeper().getCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.thereIsNoData).setPositiveButton(
					android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			builder.create().show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.clearAllDataConfirmation)
					.setMessage(R.string.clearAllDataConfirmationMessage)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									clearAllData();
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

	private final static String EXT = ".xml";

	private List<String> getFilesList() {
		List<String> list = new ArrayList<String>();
		File dir = new File(getAppDirName());
		dir.mkdirs();
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(EXT);
			}
		});
		if (files != null) {
			for (int i = 0; i < files.length; ++i) {
				String filename = files[i].getName();
				filename = filename.substring(0, filename.lastIndexOf(EXT));
				list.add(filename);
			}

			Collections.sort(list);
		}

		return list;
	}

	private static String getAppDirName() {
		String dir = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		if (dir.charAt(dir.length() - 1) != File.separatorChar) {
			dir += File.separator;
		}

		return dir + "WomanCyc" + File.separator;
	}

	private void backupAction() {
		if (getDataKeeper().getCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.thereIsNoData).setPositiveButton(
					android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			builder.create().show();
		} else {
			final List<String> list = getFilesList();
			list.add(0, getString(R.string.newFile));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.chooseFileNameToWrite)
					.setItems(list.toArray(new String[] {}),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										backupToNewFile();
									} else {
										String filename = list.get(which);
										backupWithConfirmation(filename);
									}
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

	private void backupToNewFile() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_export, null);
		final AutoCompleteTextView textView = (AutoCompleteTextView) view
				.findViewById(R.id.fileName);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getFilesList());
		textView.setAdapter(adapter);
		textView.setText(getString(R.string.bacupFileName,
				DateUtils.toString(this, Calendar.getInstance())));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.enterFileNameToWrite)
				.setView(view)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String filename = textView.getText().toString();
								if (filename.endsWith(EXT)) {
									filename = filename.substring(0,
											filename.lastIndexOf(EXT));
								}

								backupWithConfirmation(filename);
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		builder.create().show();
	}

	private void backupWithConfirmation(String filename) {
		final String absoluteFileName = getAppDirName() + filename + EXT;
		File file = new File(absoluteFileName);
		if (file.exists()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle(getString(R.string.backupConfirmation, filename))
					.setMessage(R.string.backupConfirmationMessage)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									backup(absoluteFileName);
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			builder.create().show();
		} else {
			backup(absoluteFileName);
		}
	}

	private void restoreAction() {
		final List<String> filenames = getFilesList();
		if (filenames.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.filesToReadNotFound).setPositiveButton(
					android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			builder.create().show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.chooseFileNameToRead)
					.setItems(filenames.toArray(new String[] {}),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									final String filename = filenames
											.get(which);
									restoreWithConfirmation(filename);
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

	private void restoreWithConfirmation(String filename) {
		final String absoluteFileName = getAppDirName() + filename + EXT;
		if (getDataKeeper().getCount() > 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle(getString(R.string.restoreConfirmation, filename))
					.setMessage(R.string.restoreConfirmationMessage)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									restore(absoluteFileName);
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			builder.create().show();
		} else {
			restore(absoluteFileName);
		}
	}
}