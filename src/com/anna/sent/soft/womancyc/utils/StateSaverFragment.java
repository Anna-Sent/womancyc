package com.anna.sent.soft.womancyc.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class StateSaverFragment extends Fragment implements StateSaver {
	@Override
	public final void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setViews(savedInstanceState);

		if (savedInstanceState != null) {
			restoreState(savedInstanceState);
		} else {
			savedInstanceState = getActivity().getIntent().getExtras();
			if (savedInstanceState != null) {
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
