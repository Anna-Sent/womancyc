package com.anna.sent.soft.womancyc.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends IntentService {
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

	public UpdateWidgetService() {
		super(UpdateWidgetService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// log("create widget update service");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// log("destroy widget update service");
	}

	@Override
	protected void onHandleIntent(Intent service) {
		int appWidgetId = service.getIntExtra(
				MyCycleWidget.EXTRA_APP_WIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			AppWidgetProviderInfo info = manager.getAppWidgetInfo(appWidgetId);
			if (info != null) {
				String className = info.provider.getClassName();
				Builder builder = MyCycleWidget.getBuilder(className);
				// log("update widget " + className);
				if (builder != null) {
					RemoteViews views = builder.buildViews(this, appWidgetId);
					manager.updateAppWidget(appWidgetId, views);
				}
			}
		}
	}
}