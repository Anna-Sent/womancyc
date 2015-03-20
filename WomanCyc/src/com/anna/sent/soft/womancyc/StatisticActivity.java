package com.anna.sent.soft.womancyc;

import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.anna.sent.soft.womancyc.base.ChildActivity;
import com.anna.sent.soft.womancyc.data.Calculator;
import com.anna.sent.soft.womancyc.data.Calculator.Statistic;
import com.anna.sent.soft.womancyc.data.Calculator.Value;

public class StatisticActivity extends ChildActivity implements OnClickListener {
	@Override
	public void setViews(Bundle savedInstanceState) {
		setTitle(R.string.statistic);
		setContentView(R.layout.activity_statistic);
		super.setViews(savedInstanceState);
	}

	private View mSelectedRow = null;

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		if (v instanceof TableRow) {
			if (mSelectedRow != null) {
				mSelectedRow.setBackgroundDrawable(null);
			}

			v.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.bg_selected_view));
			mSelectedRow = v;
		}
	}

	@SuppressLint("InflateParams")
	@Override
	protected void dataChanged() {
		TableRow tableRow2 = (TableRow) findViewById(R.id.tableRow2);
		tableRow2.setOnClickListener(this);
		TableRow tableRow3 = (TableRow) findViewById(R.id.tableRow3);
		tableRow3.setOnClickListener(this);
		TableRow tableRow4 = (TableRow) findViewById(R.id.tableRow4);
		tableRow4.setOnClickListener(this);

		Calculator calc = new Calculator(this, getDataKeeper());
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
			View row = inflater.inflate(R.layout.table_row_statistic, null,
					false);
			row.setOnClickListener(this);
			TextView column1 = (TextView) row.findViewById(R.id.column1);
			TextView column2 = (TextView) row.findViewById(R.id.column2);
			TextView column3 = (TextView) row.findViewById(R.id.column3);

			Date date = stat.rows.get(i).firstDayOfCycle.toDate();
			String formattedString = DateFormat.getDateFormat(this)
					.format(date);
			column1.setText(formattedString);
			int mcl = stat.rows.get(i).menstrualCycleLen;
			column2.setText(String.valueOf(mcl));
			if (mcl > Calculator.getMaxMenstrualCycleLen(this)) {
				column2.setTextColor(Color.RED);
			}

			column3.setText(String.valueOf(stat.rows.get(i).bleedingLen));
			table.addView(row, table.getChildCount() - 1);
		}

		TextView textViewStatisticRemark = (TextView) findViewById(R.id.statisticRemark);
		textViewStatisticRemark.setText(Html.fromHtml(getString(
				R.string.statisticRemark,
				Calculator.getMaxMenstrualCycleLen(this),
				Calculator.getMaxMenstrualCycleLen(this))));
	}
}