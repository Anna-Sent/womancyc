package com.anna.sent.soft.womancyc.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.anna.sent.soft.logging.MyLog;

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
        return PendingIntent.getBroadcast(context, 0,
                updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static void updateWidgets(Context context, Class<?> cls) {
        try {
            getPendingIntent(context, cls).send();
        } catch (CanceledException e) {
            MyLog.getInstance().report(new RuntimeException("failed to update widgets", e));
        }
    }

    public static void installAlarms(Context context, Class<?> cls) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        DateTime midnight = DateTime.now().withTimeAtStartOfDay().plusDays(1);
        PendingIntent operation = getPendingIntent(context, cls);
        //noinspection ConstantConditions
        alarmManager.cancel(operation);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                midnight.getMillis(), AlarmManager.INTERVAL_DAY, operation);
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
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
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
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent service = new Intent(context, UpdateWidgetService.class);
            service.putExtra(EXTRA_APP_WIDGET_ID, appWidgetId);
            context.startService(service);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //noinspection ConstantConditions
        alarmManager.cancel(getPendingIntent(context, getClass()));
    }
}
