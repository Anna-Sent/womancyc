package com.anna.sent.soft.womancyc.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class StateSaverFragment extends Fragment implements StateSaver {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	@SuppressWarnings("unused")
	private void log(String msg, boolean debug) {
		if (DEBUG && debug) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	@Override
	public final void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setViews(savedInstanceState);

		if (savedInstanceState != null) {
			log("restore 1");
			restoreState(savedInstanceState);
		} else {
			savedInstanceState = getActivity().getIntent().getExtras();
			if (savedInstanceState != null) {
				log("restore 2");
				restoreState(savedInstanceState);
			}
		}
	}

	@Override
	public final void onSaveInstanceState(Bundle outState) {
		saveState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public abstract void setViews(Bundle savedInstanceState);

	@Override
	public abstract void restoreState(Bundle state);

	@Override
	public abstract void saveState(Bundle state);
}
