package com.anna.sent.soft.womancyc;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.anna.sent.soft.womancyc.adapters.Calculator;
import com.anna.sent.soft.womancyc.adapters.Calculator.BleedingLenght;
import com.anna.sent.soft.womancyc.adapters.Calculator.MenstrualCycleLenght;
import com.anna.sent.soft.womancyc.adapters.Calculator.Statistic;
import com.anna.sent.soft.womancyc.superclasses.ChildActivity;

public class StatisticActivity extends ChildActivity {
	@Override
	public void setViews(Bundle savedInstanceState) {
		super.setViews(savedInstanceState);
		setContentView(R.layout.activity_statistic);

		Calculator calc = new Calculator(getDataKeeper());
		Statistic stat = calc.getStatistic();

		TextView avgMCL = (TextView) findViewById(R.id.rowAvgMCLValue);
		TextView minMCL = (TextView) findViewById(R.id.rowMinMCLValue);
		TextView maxMCL = (TextView) findViewById(R.id.rowMaxMCLValue);

		MenstrualCycleLenght MCL = stat.MCL;

		avgMCL.setText(String.valueOf(MCL.avg));
		minMCL.setText(String.valueOf(MCL.min));
		maxMCL.setText(String.valueOf(MCL.max));

		TextView avgBL = (TextView) findViewById(R.id.rowAvgBLValue);
		TextView minBL = (TextView) findViewById(R.id.rowMinBLValue);
		TextView maxBL = (TextView) findViewById(R.id.rowMaxBLValue);

		BleedingLenght BL = stat.BL;

		avgBL.setText(String.valueOf(BL.avg));
		minBL.setText(String.valueOf(BL.min));
		maxBL.setText(String.valueOf(BL.max));

		TableLayout table = (TableLayout) findViewById(R.id.table);
		TableRow row = new TableRow(this);
		TextView cell = new TextView(this);
		cell.setText("sdf");
		row.addView(cell);
		table.addView(row);
	}
}
