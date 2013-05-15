package com.anna.sent.soft.womancyc.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anna.sent.soft.womancyc.R;

public class SpinnerItemArrayAdapter extends ArrayAdapter<String> {
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

	private String[] mStrings;
	private int mCount;

	private static class ViewHolder {
		private TextView textView;
	}

	public SpinnerItemArrayAdapter(Context context, String[] strings) {
		super(context, R.layout.spinner_item, R.id.spinnerItemTextView, strings);
		mStrings = strings;
		mCount = mStrings.length;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup viewGroup) {
		View view;
		ViewHolder viewHolder = null;
		if (contentView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.spinner_item, null);
			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) view
					.findViewById(R.id.spinnerItemTextView);
			view.setTag(viewHolder);
		} else {
			view = contentView;
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.textView.setText(mStrings[position]);
		viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.help_light, 0, 0, 0);

		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}
}
