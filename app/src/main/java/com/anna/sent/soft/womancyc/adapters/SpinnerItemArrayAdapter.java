package com.anna.sent.soft.womancyc.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.utils.MyLog;

public class SpinnerItemArrayAdapter extends ArrayAdapter<String> {
    private String[] mStrings;
    private Drawable[] mDrawables;
    private int mCount;

    public SpinnerItemArrayAdapter(Context context, String[] strings,
                                   Drawable[] drawables) {
        super(context, R.layout.spinner_selected_item,
                R.id.spinnerItemTextView, strings);
        mStrings = strings;
        mDrawables = drawables;
        mCount = Math.min(mStrings.length, mDrawables.length);
    }

    private String wrapMsg(String msg) {
        return getClass().getSimpleName() + ": " + msg;
    }

    private void log(String msg) {
        MyLog.getInstance().logcat(Log.DEBUG, wrapMsg(msg));
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    @NonNull
    public View getView(int position, View contentView, @NonNull ViewGroup viewGroup) {
        return getView(position, contentView, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, true);
    }

    private View getView(int position, View contentView, boolean isDropDownView) {
        View view;
        ViewHolder viewHolder = null;
        if (contentView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(
                    isDropDownView ? R.layout.spinner_item
                            : R.layout.spinner_selected_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view
                    .findViewById(R.id.spinnerItemTextView);
            view.setTag(viewHolder);
        } else {
            view = contentView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textView.setText(isDropDownView ? mStrings[position] : "");
        viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(
                mDrawables[position], null, null, null);

        return view;
    }

    private static class ViewHolder {
        private TextView textView;
    }
}
