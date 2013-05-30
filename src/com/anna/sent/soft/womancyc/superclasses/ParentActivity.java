package com.anna.sent.soft.womancyc.superclasses;

import android.view.Menu;
import android.view.MenuItem;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public abstract class ParentActivity extends DataKeeperActivity {
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		switch (ThemeUtils.getThemeId(this)) {
		case ThemeUtils.LIGHT_THEME:
			menu.findItem(R.id.lighttheme).setChecked(true);
			break;
		case ThemeUtils.DARK_THEME:
			menu.findItem(R.id.darktheme).setChecked(true);
			break;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.lighttheme:
			ThemeUtils.changeToTheme(this, ThemeUtils.LIGHT_THEME);
			return true;
		case R.id.darktheme:
			ThemeUtils.changeToTheme(this, ThemeUtils.DARK_THEME);
			return true;
		case R.id.help:
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
