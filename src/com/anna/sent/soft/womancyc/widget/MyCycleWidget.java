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
import android.util.Log;

public abstract class MyCycleWidget extends AppWidgetProvider {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	@SuppressWarnings("unused")
	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	public static final String UPDATE_ACTION = "UPDATE_MY_PREGNANCY_WIDGET_ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {
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
				onUpdate(context, appWidgetManager, appWidgetIds);

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

	static final String EXTRA_APP_WIDGET_ID = "appwidgetid";

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
		updateWidget.setAction(MyCycleWidget.UPDATE_ACTION);
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