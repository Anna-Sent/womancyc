package com.anna.sent.soft.womancyc.widget;

import java.util.Calendar;

import android.content.Context;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.Calculator;
import com.anna.sent.soft.womancyc.database.DataKeeper;

public final class BuilderMedium extends Builder {
	@Override
	protected String getResult(Context context, DataKeeper dataKeeper) {
		Calculator calc = new Calculator(context, dataKeeper);
		Calendar today = Calendar.getInstance();
		int dayOfCycle = calc.getDayOfCycle(today);
		int avgLen = 0;
		if (dayOfCycle != 0) {
			avgLen = calc.getLenOfCurrentMenstrualCycle(today);
		}

		return dayOfCycle > 0 ? String.valueOf(dayOfCycle) + " ("
				+ String.valueOf(avgLen) + ")" : context
				.getString(R.string.thereIsNoData);
	}
}