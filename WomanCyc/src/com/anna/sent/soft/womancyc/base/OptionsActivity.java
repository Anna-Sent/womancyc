package com.anna.sent.soft.womancyc.base;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.anna.sent.soft.womancyc.HelpActivity;
import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.SettingsActivity;
import com.anna.sent.soft.womancyc.StatisticActivity;
import com.anna.sent.soft.womancyc.shared.Settings;

public abstract class OptionsActivity extends DataKeeperActivity {
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.lockAndExit).setVisible(
				Settings.isPasswordSet(this)
						&& !Settings.lockAutomatically(this));
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.statistic:
			startActivity(new Intent(this, StatisticActivity.class));
			return true;
		case R.id.backupData:
			backupAction();
			return true;
		case R.id.restoreData:
			restoreAction();
			return true;
		case R.id.clearAllData:
			clearAllDataAction();
			return true;
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.help:
			startActivity(new Intent(this, HelpActivity.class));
			return true;
		case R.id.lockAndExit:
			Settings.isBlocked(this, true);
			finish();
			return true;
		case R.id.test25:
			test25();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void clearAllDataAction() {
		if (getDataKeeper().getCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.thereIsNoData).setPositiveButton(
					android.R.string.ok, null);
			builder.create().show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.clearAllDataConfirmation)
					.setMessage(R.string.clearAllDataConfirmationMessage)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									clearAllData();
								}
							}).setNegativeButton(android.R.string.cancel, null);
			builder.create().show();
		}
	}

	private final static String EXT = ".xml";

	private List<String> getFilesList() {
		List<String> list = new ArrayList<String>();
		File dir = new File(getAppDirName());
		dir.mkdirs();
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(EXT);
			}
		});
		if (files != null) {
			for (int i = 0; i < files.length; ++i) {
				String filename = files[i].getName();
				filename = filename.substring(0, filename.lastIndexOf(EXT));
				list.add(filename);
			}

			Collections.sort(list);
		}

		return list;
	}

	private static String getAppDirName() {
		String dir = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		if (dir.charAt(dir.length() - 1) != File.separatorChar) {
			dir += File.separator;
		}

		return dir + "WomanCyc" + File.separator;
	}

	private void backupAction() {
		if (getDataKeeper().getCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.thereIsNoData).setPositiveButton(
					android.R.string.ok, null);
			builder.create().show();
		} else {
			final List<String> list = getFilesList();
			if (list.size() == 0) {
				backupToNewFile();
			} else {
				list.add(0, getString(R.string.newFile));
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.chooseFileNameToWrite)
						.setItems(list.toArray(new String[list.size()]),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (which == 0) {
											backupToNewFile();
										} else {
											String filename = list.get(which);
											backupWithConfirmation(filename);
										}
									}
								})
						.setNegativeButton(android.R.string.cancel, null);
				builder.create().show();
			}
		}
	}

	@SuppressLint("InflateParams")
	private void backupToNewFile() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_export, null);
		final AutoCompleteTextView textView = (AutoCompleteTextView) view
				.findViewById(R.id.fileName);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item, R.id.spinnerItemTextView, getFilesList());
		textView.setAdapter(adapter);
		textView.setText(getString(R.string.bacupFileName, LocalDate.now()
				.toString()));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.enterFileNameToWrite)
				.setView(view)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String filename = textView.getText().toString();
								if (filename.endsWith(EXT)) {
									filename = filename.substring(0,
											filename.lastIndexOf(EXT));
								}

								backupWithConfirmation(filename);
							}
						}).setNegativeButton(android.R.string.cancel, null);
		builder.create().show();
	}

	private void backupWithConfirmation(String filename) {
		final String absoluteFileName = getAppDirName() + filename + EXT;
		File file = new File(absoluteFileName);
		if (file.exists()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					OptionsActivity.this);
			builder.setTitle(getString(R.string.backupConfirmation, filename))
					.setMessage(R.string.backupConfirmationMessage)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									backup(absoluteFileName);
								}
							}).setNegativeButton(android.R.string.cancel, null);
			builder.create().show();
		} else {
			backup(absoluteFileName);
		}
	}

	private void restoreAction() {
		final List<String> filenames = getFilesList();
		if (filenames.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.filesToReadNotFound).setPositiveButton(
					android.R.string.ok, null);
			builder.create().show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.chooseFileNameToRead)
					.setItems(filenames.toArray(new String[filenames.size()]),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									final String filename = filenames
											.get(which);
									restoreWithConfirmation(filename);
								}
							}).setNegativeButton(android.R.string.cancel, null);
			builder.create().show();
		}
	}

	private void restoreWithConfirmation(String filename) {
		final String absoluteFileName = getAppDirName() + filename + EXT;
		if (getDataKeeper().getCount() > 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					OptionsActivity.this);
			builder.setTitle(getString(R.string.restoreConfirmation, filename))
					.setMessage(R.string.restoreConfirmationMessage)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									restore(absoluteFileName);
								}
							}).setNegativeButton(android.R.string.cancel, null);
			builder.create().show();
		} else {
			restore(absoluteFileName);
		}
	}
}