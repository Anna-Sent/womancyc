package com.anna.sent.soft.womancyc.widget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Define a simple widget that shows a text.
 */
public abstract class MyPregnancyWidget extends AppWidgetProvider {
	public static final String UPDATE_ACTION = "UPDATE_MY_PREGNANCY_WIDGET_ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Manual or automatic widget update started
		String action = intent.getAction();
		if (action.equals(UPDATE_ACTION)
				|| action.equals(Intent.ACTION_TIME_CHANGED)
				|| action.equals(Intent.ACTION_TIMEZONE_CHANGED)
				|| action.equals(Intent.ACTION_DATE_CHANGED)
				|| action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			int[] appWidgetIds = appWidgetManager
					.getAppWidgetIds(new ComponentName(context, getClass()));
			if (appWidgetIds.length > 0) {
				/*
				 * Log.d("moo", getClass().getSimpleName() + " got action " +
				 * action);
				 */

				onUpdate(context, appWidgetManager, appWidgetIds);

				// Need to reinstall alarm on this events
				if (action.equals(Intent.ACTION_TIME_CHANGED)
						|| action.equals(Intent.ACTION_TIMEZONE_CHANGED)
						|| action.equals(Intent.ACTION_DATE_CHANGED)
						|| action.equals(Intent.ACTION_BOOT_COMPLETED)) {
					installAlarms(context, getClass());
				}
			}
		}

		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// Update each of the widgets with the remote adapter
		for (int i = 0; i < appWidgetIds.length; ++i) {
			RemoteViews views = getBuilder().buildViews(context,
					appWidgetIds[i]);
			appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	protected abstract Builder getBuilder();

	public static void updateAllWidgets(Context context) {
		updateWidgets(context, MyPregnancyWidgetSmall.class);
		updateWidgets(context, MyPregnancyWidgetSimple.class);
	}

	private static PendingIntent getPendingIntent(Context context, Class<?> cls) {
		Intent updateWidget = new Intent(context, cls);
		updateWidget.setAction(MyPregnancyWidget.UPDATE_ACTION);
		PendingIntent result = PendingIntent.getBroadcast(context, 0,
				updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
		return result;
	}

	private static void updateWidgets(Context context, Class<?> cls) {
		try {
			getPendingIntent(context, cls).send();
		} catch (CanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		/*
		 * Log.d("moo", getClass().getSimpleName() + " cancel alarm");
		 */
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(getPendingIntent(context, getClass()));
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	public static void installAlarms(Context context, Class<?> cls) {
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Calendar midnight = Calendar.getInstance();
		midnight.set(Calendar.HOUR, 0);
		midnight.set(Calendar.MINUTE, 0);
		midnight.set(Calendar.SECOND, 0);
		midnight.set(Calendar.MILLISECOND, 0);
		midnight.set(Calendar.AM_PM, Calendar.AM);
		midnight.add(Calendar.DAY_OF_MONTH, 1);
		PendingIntent operation = getPendingIntent(context, cls);
		alarmManager.cancel(operation);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				midnight.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				operation);
	}
}