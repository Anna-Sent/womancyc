package com.anna.sent.soft.womancyc.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.anna.sent.soft.womancyc.utils.MyLog;

import org.joda.time.DateTime;

public abstract class MyCycleWidget extends AppWidgetProvider {
    public static final String UPDATE_ACTION_OLD = "UPDATE_MY_PREGNANCY_WIDGET_ACTION";
    public static final String UPDATE_ACTION_NEW = "UPDATE_MY_CYCLE_WIDGET_ACTION";
    static final String EXTRA_APP_WIDGET_ID = "appwidgetid";

    public static Builder getBuilder(String className) {
        if (MyCycleWidgetMedium.class.getName().equals(className)) {
            return new BuilderMedium();
        } else if (MyCycleWidgetSmall.class.getName().equals(className)) {
            return new BuilderSmall();
        } else {
            return null;
        }
    }

    public static void updateAllWidgets(Context context) {
        updateWidgets(context, MyCycleWidgetSmall.class);
        updateWidgets(context, MyCycleWidgetMedium.class);
    }

    private static PendingIntent getPendingIntent(Context context, Class<?> cls) {
        Intent updateWidget = new Intent(context, cls);
        updateWidget.setAction(MyCycleWidget.UPDATE_ACTION_NEW);
        PendingIntent result = PendingIntent.getBroadcast(context, 0,
                updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
        return result;
    }

    private static void updateWidgets(Context context, Class<?> cls) {
        try {
            getPendingIntent(context, cls).send();
        } catch (CanceledException e) {
            e.printStackTrace();
        }
    }

    public static void installAlarms(Context context, Class<?> cls) {
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        DateTime midnight = DateTime.now().withTimeAtStartOfDay().plusDays(1);
        PendingIntent operation = getPendingIntent(context, cls);
        alarmManager.cancel(operation);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                midnight.getMillis(), AlarmManager.INTERVAL_DAY, operation);
    }

    private String wrapMsg(String msg) {
        return getClass().getSimpleName() + ": " + msg;
    }

    private void log(String msg) {
        MyLog.getInstance().logcat(Log.DEBUG, wrapMsg(msg));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(UPDATE_ACTION_OLD)
                    || action.equals(UPDATE_ACTION_NEW)
                    || action.equals(Intent.ACTION_TIME_CHANGED)
                    || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                    || action.equals(Intent.ACTION_DATE_CHANGED)
                    || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                AppWidgetManager appWidgetManager = AppWidgetManager
                        .getInstance(context);
                int[] appWidgetIds = appWidgetManager
                        .getAppWidgetIds(new ComponentName(context, getClass()));
                if (appWidgetIds.length > 0) {
                    onUpdate(context, appWidgetManager, appWidgetIds);

                    if (action.equals(Intent.ACTION_TIME_CHANGED)
                            || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                            || action.equals(Intent.ACTION_DATE_CHANGED)
                            || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                        installAlarms(context, getClass());
                    }
                }
            }
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent service = new Intent(context, UpdateWidgetService.class);
            service.putExtra(EXTRA_APP_WIDGET_ID, appWidgetIds[i]);
            context.startService(service);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(context, getClass()));
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }
}
