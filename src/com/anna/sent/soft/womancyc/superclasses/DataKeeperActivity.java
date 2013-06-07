package com.anna.sent.soft.womancyc.superclasses;

import java.util.Calendar;
import java.util.List;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.database.DataKeeperInterface;
import com.anna.sent.soft.womancyc.widget.MyCycleWidget;

public abstract class DataKeeperActivity extends StateSaverActivity implements
		DataKeeperInterface {
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

	private DataKeeper mDataKeeper;

	@Override
	public void setViews(Bundle savedInstanceState) {
		super.setViews(savedInstanceState);
		mDataKeeper = new DataKeeper(this);
		openDataSource();
	}

	@Override
	protected void onResume() {
		openDataSource();
		super.onResume();
	}

	private void openDataSource() {
		try {
			mDataKeeper.openDataSource();
		} catch (SQLException e) {
			e.printStackTrace();
			Toast.makeText(this,
					getString(R.string.errorWhileOpenningDatabase),
					Toast.LENGTH_LONG).show();
			mDataKeeper.closeDataSource();
		}

		dataChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mDataKeeper.closeDataSource();
		MyCycleWidget.updateAllWidgets(this);
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
		try {
			mDataKeeper.clearAllData();
		} catch (SQLException e) {
			e.printStackTrace();
			Toast.makeText(this,
					getString(R.string.errorWhileOpenningDatabase),
					Toast.LENGTH_LONG).show();
			mDataKeeper.closeDataSource();
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
