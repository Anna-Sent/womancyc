package com.anna.sent.soft.womancyc.widget;

import java.util.Calendar;

import android.content.Context;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.Calculator;
import com.anna.sent.soft.womancyc.database.DataKeeperImpl;

public class BuilderSmall extends Builder {
	@Override
	protected String getResult(Context context, DataKeeperImpl dataKeeper) {
		Calculator calc = new Calculator(context, dataKeeper);
		Calendar today = Calendar.getInstance();
		int dayOfCycle = calc.getDayOfCycle(today);
		return dayOfCycle > 0 ? String.valueOf(dayOfCycle) : context
				.getString(R.string.noData);
	}
}
