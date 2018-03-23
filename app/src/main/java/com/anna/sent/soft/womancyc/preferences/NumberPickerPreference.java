package com.anna.sent.soft.womancyc.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.anna.sent.soft.logging.MyLog;
import com.anna.sent.soft.womancyc.R;

public class NumberPickerPreference extends DialogPreference {
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_VALUE = 0;
    private int mMinValue;
    private int mMaxValue;
    private int mValue;
    private String mUnit;
    private NumberPicker mNumberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.NumberPickerPreference, 0, 0);
        try {
            setMinValue(a.getInteger(R.styleable.NumberPickerPreference_min,
                    DEFAULT_MIN_VALUE));
            setMaxValue(a.getInteger(
                    R.styleable.NumberPickerPreference_android_max,
                    DEFAULT_MAX_VALUE));
            mUnit = a.getString(R.styleable.NumberPickerPreference_unit);
        } finally {
            a.recycle();
        }

        setDialogLayoutResource(R.layout.dialog_numberpicker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    private String wrapMsg(String msg) {
        return getClass().getSimpleName() + ": " + msg;
    }

    private void log(String msg) {
        MyLog.getInstance().logcat(Log.DEBUG, wrapMsg(msg));
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        setValue(restore ? getPersistedInt(DEFAULT_VALUE)
                : (Integer) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_VALUE);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        TextView unit = view.findViewById(R.id.textViewUnit);
        unit.setText(mUnit);

        mNumberPicker = view.findViewById(R.id.numberPicker);
        mNumberPicker.setMinValue(mMinValue);
        mNumberPicker.setMaxValue(mMaxValue);
        mNumberPicker.setValue(mValue);
    }

    public void setMinValue(int minValue) {
        mMinValue = minValue;
        setValue(Math.max(mValue, mMinValue));
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        setValue(Math.min(mValue, mMaxValue));
    }

    public void setValue(int value) {
        value = Math.max(Math.min(value, mMaxValue), mMinValue);

        if (value != mValue) {
            mValue = value;
            persistInt(value);
            notifyChanged();
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            mNumberPicker.clearFocus();
            int numberPickerValue = mNumberPicker.getValue();
            if (callChangeListener(numberPickerValue)) {
                setValue(numberPickerValue);
                mNumberPicker = null;
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        log("save");
        final Parcelable superState = super.onSaveInstanceState();

        final SavedState myState = new SavedState(superState);
        if (mNumberPicker != null) {
            myState.value = mNumberPicker.getValue();
            log("save " + myState.value);
        }

        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        log("restore");
        if (state == null || !state.getClass().equals(SavedState.class)) {
            log("restore " + (state == null ? "null" : state.getClass().getName()));
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (mNumberPicker != null) {
            mNumberPicker.setValue(myState.value);
            log("restore " + myState.value);
        }
    }

    private static class SavedState extends BaseSavedState {
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }
    }
}
