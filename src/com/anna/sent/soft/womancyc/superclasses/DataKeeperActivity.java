package com.anna.sent.soft.womancyc.superclasses;

import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

	@SuppressWarnings("unused")
	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private DataKeeperImpl mDataKeeper;

	@Override
	public void setViews(Bundle savedInstanceState) {
		super.setViews(savedInstanceState);
		mDataKeeper = new DataKeeperImpl(this);
		openDataSource();
	}

	@Override
	protected void onResume() {
		openDataSource();
		super.onResume();
	}

	private void _openDataSource() {
		try {
			mDataKeeper.openDataSource();
		} catch (SQLException e) {
			e.printStackTrace();
			Toast.makeText(this,
					getString(R.string.errorWhileOpenningDatabase),
					Toast.LENGTH_LONG).show();
		}
	}

	private void openDataSource() {
		new StartupTask().execute();
		dataChanged();
	}

	private class StartupTask extends AsyncTask<Object, Object, Object> {
		private ProgressDialog progressDialog;

		@Override
		protected Object doInBackground(Object... objects) {
			_openDataSource();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			runOnUiThread(new Runnable() {
				public void run() {
					progressDialog = ProgressDialog.show(
							DataKeeperActivity.this, "", "", true);
				}
			});
		}

		@Override
		protected void onPostExecute(Object object) {
			super.onPostExecute(object);
			progressDialog.dismiss();
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
		mDataKeeper.clearAllData();
		dataChanged();
	}

	protected void backup() {
		CalendarDataManager cdm = new CalendarDataManager(this);
		boolean result = cdm.backup(mDataKeeper);
		if (result) {
			Toast.makeText(
					this,
					getString(R.string.dataExportSuccessfull,
							CalendarDataManager.getBackupFileName()),
					Toast.LENGTH_LONG).show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(cdm.getErrorMessage()).setPositiveButton(
					android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			builder.create().show();
		}
	}

	protected void restore() {
		mDataKeeper.clearAllData();
		CalendarDataManager cdm = new CalendarDataManager(this);
		boolean result = cdm.restore(mDataKeeper);
		if (result) {
			Toast.makeText(
					this,
					getString(R.string.dataImportSuccessfull,
							CalendarDataManager.getBackupFileName()),
					Toast.LENGTH_LONG).show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(cdm.getErrorMessage()).setPositiveButton(
					android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			builder.create().show();
		}

		dataChanged();
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof DataKeeperClient) {
			((DataKeeperClient) fragment).setDataKeeper(this);
		}
	}
}
