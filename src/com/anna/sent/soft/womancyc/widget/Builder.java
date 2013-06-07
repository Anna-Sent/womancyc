package com.anna.sent.soft.womancyc.widget;

import java.util.Calendar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.anna.sent.soft.womancyc.DayViewActivityDark;
import com.anna.sent.soft.womancyc.DayViewActivityLight;
import com.anna.sent.soft.womancyc.MainActivity;
import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public abstract class Builder {
	private void setOnClickPendingIntent(Context context, RemoteViews views) {
		Intent intent;
		PendingIntent pendingIntent;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| context.getResources().getBoolean(R.bool.isLargeLayout)) {
			intent = new Intent(context, MainActivity.class);
			pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		} else {
			intent = new Intent(
					context,
					ThemeUtils.DARK_THEME == ThemeUtils.getThemeId(context) ? DayViewActivityDark.class
							: DayViewActivityLight.class);
			intent.putExtra(Shared.DATE_TO_SHOW, Calendar.getInstance());
			intent.putExtra("setResult", "");
			pendingIntent = TaskStackBuilder.create(context)
					.addNextIntentWithParentStack(intent)
					.getPendingIntent(0, 0);
		}

		views.setOnClickPendingIntent(R.id.widget, pendingIntent);
	}

	public RemoteViews buildViews(Context context, int appWidgetId) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		setOnClickPendingIntent(context, views);

		String result;
		DataKeeper dataKeeper = new DataKeeper(context);
		try {
			dataKeeper.openDataSource();
			result = getResult(context, dataKeeper);
		} catch (SQLException e) {
			result = context.getString(R.string.errorWhileOpenningDatabase);
		} finally {
			dataKeeper.closeDataSource();
		}

		views.setTextViewText(R.id.widgetTextView, result);
		return views;
	}

	protected abstract String getResult(Context context, DataKeeper dataKeeper);
}