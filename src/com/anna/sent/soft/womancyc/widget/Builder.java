package com.anna.sent.soft.womancyc.widget;

import java.util.Calendar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.anna.sent.soft.womancyc.DayViewActivity;
import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.shared.Shared;

public abstract class Builder {
	private void setOnClickPendingIntent(Context context, RemoteViews views) {
		Intent detailsIntent = new Intent(context, DayViewActivity.class);
		detailsIntent.putExtra(Shared.DATE_TO_SHOW, Calendar.getInstance());
		PendingIntent pendingIntent = TaskStackBuilder.create(context)
				.addNextIntentWithParentStack(detailsIntent)
				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.widget, pendingIntent);
	}

	public RemoteViews buildViews(Context context, int appWidgetId) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		setOnClickPendingIntent(context, views);
		views.setTextViewText(R.id.widgetTextView, "23 (34)");
		return views;
	}
}