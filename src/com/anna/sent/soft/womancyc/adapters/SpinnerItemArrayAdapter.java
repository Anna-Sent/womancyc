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
	private int[] mImageResourceIds;
	private int mCount;

	private static class ViewHolder {
		private TextView textView;
	}

	public SpinnerItemArrayAdapter(Context context, String[] strings,
			int[] imageResourceIds) {
		super(context, R.layout.spinner_item, R.id.spinnerItemTextView, strings);
		mStrings = strings;
		mImageResourceIds = imageResourceIds;
		mCount = Math.min(mStrings.length, mImageResourceIds.length);
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup viewGroup) {
		return getView(position, contentView, false);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, true);
	}

	private View getView(int position, View contentView, boolean isDropDownView) {
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

		viewHolder.textView.setText(isDropDownView ? mStrings[position] : "");
		viewHolder.textView.setCompoundDrawablePadding(5);
		viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(
				mImageResourceIds[position], 0, 0, 0);

		return view;
	}
}
