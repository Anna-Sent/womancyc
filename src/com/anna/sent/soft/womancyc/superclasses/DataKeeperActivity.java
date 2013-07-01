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
import com.anna.sent.soft.womancyc.widget.MyCycleWidget;

public abstract class DataKeeperActivity extends StateSaverActivity implements
		DataKeeper {
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

	private abstract class DataTask extends AsyncTask<String, Object, String> {
		private ProgressDialog mProgressDialog = null;
		private Timer mTimer = new Timer();
		private boolean mCompleted = false;
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
			if (mShowProgress) {
				mTimer.schedule(new ShowProgressTask(), 500);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			log("onPostExecute");
			mCompleted = true;
			mTimer.cancel();
			mTimer.purge();
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}

			if (result != null && !result.equals("")) {
				Toast.makeText(DataKeeperActivity.this, result,
						Toast.LENGTH_LONG).show();
			}
		}

		private class ShowProgressTask extends TimerTask {
			@Override
			public void run() {
				log("ShowProgressTask");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mCompleted) {
							return;
						}

						log("ShowProgressTask on ui thread");
						mProgressDialog = ProgressDialog.show(
								DataKeeperActivity.this, "", mProgressMessage,
								false, false);
					}
				});
			}
		}
	}

	private DataKeeperImpl mDataKeeper;

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

	private void openDataSource() {
		log("before task execute");
		new OpenDataSourceTask().execute();
		log("after task execute");
	}

	private class OpenDataSourceTask extends DataTask {
		public OpenDataSourceTask() {
			super(true, "Open data source");
		}

		@Override
		protected String doInBackground(String... params) {
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
			dataLoaded();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeDataSource();
		MyCycleWidget.updateAllWidgets(this);
	}

	private void closeDataSource() {
		mDataKeeper.closeDataSource();
	}

	protected abstract void dataChanged();

	protected abstract void dataLoaded();

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

	protected final void clearAllData() {
		new ClearAllDataTask().execute();
	}

	private class ClearAllDataTask extends DataTask {
		public ClearAllDataTask() {
			super(true, "Clear all data");
		}

		@Override
		protected String doInBackground(String... params) {
			mDataKeeper.clearAllData();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
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
			super(true, "Restore data");
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
