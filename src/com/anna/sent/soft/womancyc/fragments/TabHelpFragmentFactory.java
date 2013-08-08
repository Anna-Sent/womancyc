package com.anna.sent.soft.womancyc.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.R;

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

	public abstract static class TabHelpFragment extends Fragment {
		private TextView textViewHelp;

		public TabHelpFragment() {
			super();
		}

		protected abstract int getLayoutResourceId();

		protected abstract int getPosition();

		protected abstract int getTextViewResourceId();

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(getLayoutResourceId(), container, false);
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			textViewHelp = (TextView) getActivity().findViewById(
					getTextViewResourceId());
			String[] helpParts = getResources().getStringArray(
					R.array.helpParts);
			int position = getPosition();
			if (position >= 0 && position < helpParts.length) {
				textViewHelp.setText(Html.fromHtml(helpParts[position]));
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
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			Button buttonSupport = (Button) getActivity().findViewById(
					R.id.buttonSupport);
			buttonSupport.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_SENDTO);
					intent.setData(Uri.parse(getString(R.string.supportData)));
					intent.putExtra(Intent.EXTRA_SUBJECT,
							getString(R.string.app_name));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					if (intent.resolveActivity(getActivity()
							.getPackageManager()) != null) {
						startActivity(intent);
					} else {
						Toast.makeText(getActivity(),
								R.string.sendto_app_not_available,
								Toast.LENGTH_LONG).show();
					}
				}
			});
		}

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
