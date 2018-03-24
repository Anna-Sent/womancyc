package com.anna.sent.soft.womancyc.base;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.database.CalendarDataManager;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.database.DataKeeperImpl;
import com.anna.sent.soft.womancyc.widget.MyCycleWidget;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("StaticFieldLeak")
public abstract class DataKeeperActivity extends WcActivity implements DataKeeper {
    private boolean mIsDataTaskCompleted;
    private ProgressDialog mProgressDialog;
    private Timer mTimer;
    private DataKeeperImpl mDataKeeper;
    private boolean mIsStopped;

    private void startTimer(String message) {
        stopTimer();
        mTimer = new Timer(message);
        mTimer.schedule(new StartProgressTask(message), 500);
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

    protected DataKeeper getDataKeeper() {
        return mDataKeeper;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        super.onDestroy();
        closeDataSource();
    }

    private void closeDataSource() {
        mDataKeeper.closeDataSource();
    }

    protected abstract void dataChanged();

    @Override
    public CalendarData get(LocalDate date) {
        return mDataKeeper.get(date);
    }

    @Override
    public int indexOf(LocalDate date) {
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

    protected final void clearAllData() {
        new ClearAllDataTask().execute();
    }

    protected void backup(String filename) {
        new BackupTask().execute(filename);
    }

    protected void restore(String filename) {
        new RestoreTask().execute(filename);
    }

    protected void test25() {
        new Test25YearsTask().execute();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof DataKeeperClient) {
            ((DataKeeperClient) fragment).setDataKeeper(this);
        }
    }

    private class StartProgressTask extends TimerTask {
        private final String mTitle;

        StartProgressTask(String title) {
            mTitle = title;
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
                                DataKeeperActivity.this, mTitle, "", false,
                                false);
                    }
                }
            });
        }
    }

    private abstract class DataTask extends AsyncTask<String, String, String> {
        private final boolean mShowProgress;
        private final String mProgressMessage;

        DataTask(boolean showProgress, String progressMessage) {
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
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                Toast.makeText(DataKeeperActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class OpenDataSourceTask extends DataTask {
        OpenDataSourceTask() {
            super(true, getString(R.string.openDataSourceTask));
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mDataKeeper.openDataSource();
            } catch (SQLException e) {
                e.printStackTrace();
                return getString(R.string.errorWhileOpeningDatabase);
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

    private class ClearAllDataTask extends DataTask {
        ClearAllDataTask() {
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

    private class BackupTask extends DataTask {
        BackupTask() {
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
                return getString(R.string.dataExportSuccessful, filename);
            } else {
                return cdm.getErrorMessage();
            }
        }
    }

    private class RestoreTask extends DataTask {
        RestoreTask() {
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
                return getString(R.string.dataImportSuccessful, filename);
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

    private class Test25YearsTask extends DataTask {
        Test25YearsTask() {
            super(true, getString(R.string.test25));
        }

        @Override
        protected String doInBackground(String... params) {
            LocalDate today = LocalDate.now();
            org.joda.time.LocalDate date = today;
            date = date.minusYears(25);
            int initialYear = date.getYear();
            int prevYear = initialYear;
            int index = 1;
            while (date.isBefore(today) || date.isEqual(today)) {
                int currentYear = date.getYear();
                log(index + " " + date);
                CalendarData value = new CalendarData(date);
                value.setMenstruation(1 <= index && index <= 7 ? 1 : 0);
                value.setSex(index % 7 == 0 ? 1 : 0);
                value.setTookPill(currentYear % 2 == 0);
                value.setNote(index % 4 == 0 ? "note " + index : "");
                mDataKeeper.insertOrUpdate(value);

                ++index;
                if (index == 29) {
                    index = 1;
                }

                date = date.plusDays(1);
                if (currentYear != prevYear) {
                    String progress = String.valueOf(currentYear - initialYear);
                    publishProgress(progress);
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
}
