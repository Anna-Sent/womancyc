package com.anna.sent.soft.womancyc.widget;

import org.joda.time.LocalDate;

import android.content.Context;

import com.anna.sent.soft.womancyc.data.Calculator;
import com.anna.sent.soft.womancyc.database.DataKeeper;

public final class BuilderSmall extends Builder {
	@Override
	protected String getResult(Context context, DataKeeper dataKeeper) {
		Calculator calc = new Calculator(context, dataKeeper);
		LocalDate today = LocalDate.now();
		int dayOfCycle = calc.getDayOfCycle(today);
		return dayOfCycle > 0 ? String.valueOf(dayOfCycle) : THERE_IS_NO_DATA;
	}
}