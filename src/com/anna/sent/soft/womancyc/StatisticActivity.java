package com.anna.sent.soft.womancyc;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.anna.sent.soft.womancyc.data.Calculator;
import com.anna.sent.soft.womancyc.data.Calculator.Statistic;
import com.anna.sent.soft.womancyc.data.Calculator.Value;
import com.anna.sent.soft.womancyc.superclasses.ChildActivity;
import com.anna.sent.soft.womancyc.utils.DateUtils;

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

		Value MCL = stat.MCL;

		avgMCL.setText(String.valueOf(MCL.avg));
		minMCL.setText(String.valueOf(MCL.min));
		maxMCL.setText(String.valueOf(MCL.max));

		TextView avgBL = (TextView) findViewById(R.id.rowAvgBLValue);
		TextView minBL = (TextView) findViewById(R.id.rowMinBLValue);
		TextView maxBL = (TextView) findViewById(R.id.rowMaxBLValue);

		Value BL = stat.BL;

		avgBL.setText(String.valueOf(BL.avg));
		minBL.setText(String.valueOf(BL.min));
		maxBL.setText(String.valueOf(BL.max));

		TableLayout table = (TableLayout) findViewById(R.id.table);
		for (int i = 0; i < stat.rows.size(); ++i) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.statistic_row, null, false);
			TextView column1 = (TextView) row.findViewById(R.id.column1);
			TextView column2 = (TextView) row.findViewById(R.id.column2);
			TextView column3 = (TextView) row.findViewById(R.id.column3);

			column1.setText(DateUtils.toString(stat.rows.get(i).firstDayOfCycle));
			int mcl = stat.rows.get(i).menstrualCycleLen;
			column2.setText(String.valueOf(mcl));
			if (mcl > 60) {
				column2.setTextColor(Color.RED);
			}

			column3.setText(String.valueOf(stat.rows.get(i).bleedingLen));
			table.addView(row);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.statistic, menu);
		menu.findItem(R.id.showDividers).setChecked(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.showDividers:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
