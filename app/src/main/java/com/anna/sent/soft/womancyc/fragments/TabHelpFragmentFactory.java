package com.anna.sent.soft.womancyc.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anna.sent.soft.utils.HtmlUtils;
import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.base.WcFragment;

public class TabHelpFragmentFactory {
    public static Fragment newInstance(int position) {
        switch (position) {
            case 0:
                return new TabHelpFragment0();
            case 1:
                return new TabHelpFragment1();
            case 2:
                return new TabHelpFragment2();
            case 3:
                return new TabHelpFragment3();
            case 4:
                return new TabHelpFragment4();
        }

        return new Fragment();
    }

    public abstract static class TabHelpFragment extends WcFragment {
        private TextView textViewHelp;

        protected abstract int getLayoutResourceId();

        protected abstract int getPosition();

        protected abstract int getTextViewResourceId();

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(getLayoutResourceId(), container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            //noinspection ConstantConditions
            textViewHelp = getActivity().findViewById(getTextViewResourceId());
            String[] helpParts = getResources().getStringArray(R.array.helpParts);
            int position = getPosition();
            if (position >= 0 && position < helpParts.length) {
                textViewHelp.setText(HtmlUtils.fromHtml(helpParts[position]));
            } else {
                textViewHelp.setText("");
            }
        }
    }

    public static class TabHelpFragment0 extends TabHelpFragment {
        @Override
        protected int getLayoutResourceId() {
            return R.layout.view_help_0;
        }

        @Override
        protected int getPosition() {
            return 0;
        }

        @Override
        protected int getTextViewResourceId() {
            return R.id.textViewHelp0;
        }
    }

    public static class TabHelpFragment1 extends TabHelpFragment {
        @Override
        protected int getLayoutResourceId() {
            return R.layout.view_help_1;
        }

        @Override
        protected int getPosition() {
            return 1;
        }

        @Override
        protected int getTextViewResourceId() {
            return R.id.textViewHelp1;
        }
    }

    public static class TabHelpFragment2 extends TabHelpFragment {
        @Override
        protected int getLayoutResourceId() {
            return R.layout.view_help_2;
        }

        @Override
        protected int getPosition() {
            return 2;
        }

        @Override
        protected int getTextViewResourceId() {
            return R.id.textViewHelp2;
        }
    }

    public static class TabHelpFragment3 extends TabHelpFragment {
        @Override
        protected int getLayoutResourceId() {
            return R.layout.view_help_3;
        }

        @Override
        protected int getPosition() {
            return 3;
        }

        @Override
        protected int getTextViewResourceId() {
            return R.id.textViewHelp3;
        }
    }

    public static class TabHelpFragment4 extends TabHelpFragment {
        @Override
        protected int getLayoutResourceId() {
            return R.layout.view_help_4;
        }

        @Override
        protected int getPosition() {
            return 4;
        }

        @Override
        protected int getTextViewResourceId() {
            return R.id.textViewHelp4;
        }
    }
}
