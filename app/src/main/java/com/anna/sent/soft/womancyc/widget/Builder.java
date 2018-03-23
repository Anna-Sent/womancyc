package com.anna.sent.soft.womancyc.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;
import android.widget.RemoteViews;

import com.anna.sent.soft.logging.MyLog;
import com.anna.sent.soft.womancyc.PasswordActivity;
import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.database.DataKeeperImpl;
import com.anna.sent.soft.womancyc.shared.Settings;

abstract class Builder {
    protected static final String THERE_IS_NO_DATA = "";
    private static final String APP_IS_LOCKED = "?";

    private String wrapMsg(String msg) {
        return getClass().getSimpleName() + ": " + msg;
    }

    private void log(String msg) {
        MyLog.getInstance().logcat(Log.DEBUG, wrapMsg(msg));
    }

    private void setOnClickPendingIntent(Context context, RemoteViews views) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, PasswordActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
    }

    public RemoteViews buildViews(Context context) {
        log("build views");
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        setOnClickPendingIntent(context, views);

        String result;
        if (Settings.hideWidget(context)
                && Settings.isApplicationLocked(context)) {
            result = APP_IS_LOCKED;
        } else {
            DataKeeperImpl dataKeeper = new DataKeeperImpl(context);
            try {
                dataKeeper.openDataSource();
                result = getResult(context, dataKeeper);
            } catch (SQLException e) {
                result = context.getString(R.string.errorWhileOpeningDatabase);
            } finally {
                dataKeeper.closeDataSource();
            }
        }

        log(result);
        views.setTextViewText(R.id.widgetTextView, result);
        return views;
    }

    protected abstract String getResult(Context context, DataKeeper dataKeeper);
}
