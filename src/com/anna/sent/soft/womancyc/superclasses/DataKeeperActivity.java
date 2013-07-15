package com.anna.sent.soft.womancyc.superclasses;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.CalendarDataManager;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.database.DataKeeperImpl;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.widget.MyCycleWidget;

public abstract class DataKeeperActivity extends StateSaverActivity implements
		DataKeeper {
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

	private boolean mIsDataTaskCompleted;
	private ProgressDialog mProgressDialog = null;
	private Timer mTimer = null;

	private void startTimer(String message) {
		stopTimer();
		mTimer = new Timer(message);
		mTimer.schedule(new StartProgressTask(message), 500);
	}

	private class StartProgressTask extends TimerTask {
		private String mMessage;

		public StartProgressTask(String message) {
			super();
			mMessage = message;
		}

		@Override
		public void run() {
			log("ShowProgressTask");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mIsDataTaskCompleted || mIsStopped) {
						return;
					}

					log("ShowProgressTask on ui thread");
					if (mProgressDialog == null) {
						mProgressDialog = ProgressDialog.show(
								DataKeeperActivity.this, mMessage, mMessage,
								false, false);
					}
				}
			});
		}
	}

	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}
	}

	private void stopProgress() {
		if (mProgressDialog != null && !mIsStopped) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	private abstract class DataTask extends AsyncTask<String, String, String> {
		private boolean mShowProgress;
		private String mProgressMessage;

		public DataTask(boolean showProgress, String progressMessage) {
			super();
			mShowProgress = showProgress;
			mProgressMessage = progressMessage;
		}

		@Override
		protected void onPreExecute() {
			log("onPreExecute");
			mIsDataTaskCompleted = false;
			if (mShowProgress) {
				startTimer(mProgressMessage);
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			if (mShowProgress && !mIsStopped && mProgressDialog != null) {
				String progress = values.length > 0 && values[0] != null ? values[0]
						: "";
				mProgressDialog.setMessage(progress);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			log("onPostExecute");
			mIsDataTaskCompleted = true;
			stopTimer();
			stopProgress();
			if (mIsStopped) {
				closeDataSource();
			} else {
				if (result != null && !result.equals("")) {
					Toast.makeText(DataKeeperActivity.this, result,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private DataKeeperImpl mDataKeeper;

	protected DataKeeper getDataKeeper() {
		return mDataKeeper;
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		super.setViews(savedInstanceState);
		mDataKeeper = new DataKeeperImpl(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		openDataSource();
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeDataSource();
		MyCycleWidget.updateAllWidgets(this);
	}

	private boolean mIsStopped;

	@Override
	protected void onStart() {
		super.onStart();
		mIsStopped = false;
	}

	@Override
	protected void onStop() {
		stopTimer();
		stopProgress();
		closeDataSource();
		mIsStopped = true;
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		closeDataSource();
		super.onDestroy();
	}

	private void closeDataSource() {
		mDataKeeper.closeDataSource();
	}

	protected abstract void dataChanged();

	@Override
	public CalendarData get(Calendar date) {
		return mDataKeeper.get(date);
	}

	@Override
	public int indexOf(Calendar date) {
		return mDataKeeper.indexOf(date);
	}

	@Override
	public CalendarData get(int index) {
		return mDataKeeper.get(index);
	}

	@Override
	public int getCount() {
		return mDataKeeper.getCount();
	}

	@Override
	public void insertOrUpdate(CalendarData value) {
		mDataKeeper.insertOrUpdate(value);
		dataChanged();
	}

	@Override
	public void delete(CalendarData value) {
		mDataKeeper.delete(value);
		dataChanged();
	}

	@Override
	public List<String> getNotes() {
		return mDataKeeper.getNotes();
	}

	private void openDataSource() {
		log("before open data source task execute");
		new OpenDataSourceTask().execute();
		log("after open data source task execute");
	}

	private class OpenDataSourceTask extends DataTask {
		public OpenDataSourceTask() {
			super(true, getString(R.string.openDataSourceTask));
		}

		@Override
		protected String doInBackground(String... params) {
			/*
			 * try { Thread.sleep(1000); } catch (InterruptedException e1) {
			 * e1.printStackTrace(); }
			 */
			try {
				mDataKeeper.openDataSource();
			} catch (SQLException e) {
				e.printStackTrace();
				return getString(R.string.errorWhileOpenningDatabase);
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mIsStopped) {
				return;
			}

			dataChanged();
		}
	}

	protected final void clearAllData() {
		new ClearAllDataTask().execute();
	}

	private class ClearAllDataTask extends DataTask {
		public ClearAllDataTask() {
			super(true, getString(R.string.clearAllData));
		}

		@Override
		protected String doInBackground(String... params) {
			mDataKeeper.clearAllData();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mIsStopped) {
				return;
			}

			dataChanged();
		}
	}

	protected void backup(String filename) {
		new BackupTask().execute(filename);
	}

	private class BackupTask extends DataTask {
		public BackupTask() {
			super(false, null);
		}

		@Override
		protected String doInBackground(String... params) {
			String filename = params.length > 0 && params[0] != null ? params[0]
					: "";
			CalendarDataManager cdm = new CalendarDataManager(
					DataKeeperActivity.this);
			boolean result = cdm.backup(mDataKeeper, filename);
			if (result) {
				return getString(R.string.dataExportSuccessfull, filename);
			} else {
				return cdm.getErrorMessage();
			}
		}
	}

	protected void restore(String filename) {
		new RestoreTask().execute(filename);
	}

	private class RestoreTask extends DataTask {
		public RestoreTask() {
			super(true, getString(R.string.restoreDataTask));
		}

		@Override
		protected String doInBackground(String... params) {
			String filename = params.length > 0 && params[0] != null ? params[0]
					: "";
			mDataKeeper.clearAllData();
			CalendarDataManager cdm = new CalendarDataManager(
					DataKeeperActivity.this);
			boolean result = cdm.restore(mDataKeeper, filename);
			if (result) {
				return getString(R.string.dataImportSuccessfull, filename);
			} else {
				return cdm.getErrorMessage();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mIsStopped) {
				return;
			}

			dataChanged();
		}
	}

	protected void test25() {
		new Test25YearsTask().execute();
	}

	private class Test25YearsTask extends DataTask {
		public Test25YearsTask() {
			super(true, getString(R.string.test25));
		}

		@Override
		protected String doInBackground(String... params) {
			Calendar today = Calendar.getInstance();
			Calendar date = (Calendar) today.clone();
			date.add(Calendar.YEAR, -25);
			int initialYear = date.get(Calendar.YEAR);
			int prevYear = initialYear;
			int index = 1;
			while (DateUtils.beforeOrEqual(date, today)) {
				log(index + " " + DateUtils.toString(date));
				if (1 <= index && index <= 7) {
					CalendarData value = new CalendarData(date);
					value.setMenstruation(1);
					mDataKeeper.insertOrUpdate(value);
				}

				++index;
				if (index == 29) {
					index = 1;
				}

				date.add(Calendar.DAY_OF_MONTH, 1);

				int currentYear = date.get(Calendar.YEAR);
				if (currentYear != prevYear) {
					String progress = String.valueOf(currentYear - initialYear);
					publishProgress(new String[] { progress });
					prevYear = currentYear;
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mIsStopped) {
				return;
			}

			dataChanged();
		}
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof DataKeeperClient) {
			((DataKeeperClient) fragment).setDataKeeper(this);
		}
	}
}
