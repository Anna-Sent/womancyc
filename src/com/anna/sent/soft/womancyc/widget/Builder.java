package com.anna.sent.soft.womancyc.widget;

import java.util.Calendar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.anna.sent.soft.womancyc.DayViewActivityDark;
import com.anna.sent.soft.womancyc.DayViewActivityLight;
import com.anna.sent.soft.womancyc.MainActivity;
import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public abstract class Builder {
	private void setOnClickPendingIntent(Context context, RemoteViews views) {
		Intent dayViewIntent = new Intent(
				context,
				ThemeUtils.DARK_THEME == ThemeUtils.getThemeId(context) ? DayViewActivityDark.class
						: DayViewActivityLight.class);
		dayViewIntent.putExtra(Shared.DATE_TO_SHOW, Calendar.getInstance());
		Intent monthViewIntent = new Intent(context, MainActivity.class);

		PendingIntent pendingIntent;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| context.getResources().getBoolean(R.bool.isLargeLayout)) {
			pendingIntent = PendingIntent.getActivity(context, 0,
					monthViewIntent, 0);
		} else {
			pendingIntent = TaskStackBuilder
					.create(context)
					.addNextIntentWithParentStack(dayViewIntent)
					.getPendingIntent(MainActivity.REQUEST_DATE,
							PendingIntent.FLAG_UPDATE_CURRENT);
		}

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