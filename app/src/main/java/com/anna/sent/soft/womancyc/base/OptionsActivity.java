package com.anna.sent.soft.womancyc.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.HelpActivity;
import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.SettingsActivity;
import com.anna.sent.soft.womancyc.StatisticActivity;
import com.anna.sent.soft.womancyc.shared.Settings;

import org.joda.time.LocalDate;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class OptionsActivity extends DataKeeperActivity {
    private final static String EXT = ".xml";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2;

    private static String getAppDirName() {
        String dir = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        if (dir.charAt(dir.length() - 1) != File.separatorChar) {
            dir += File.separator;
        }

        return dir + "WomanCyc" + File.separator;
    }

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
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.statistic:
                startActivity(new Intent(this, StatisticActivity.class));
                return true;
            case R.id.backupData:
                checkPermissionForAction(itemId);
                return true;
            case R.id.restoreData:
                checkPermissionForAction(itemId);
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

    private void checkPermissionForAction(final int itemId) {
        final String permission;
        final int requestCode;
        final int requestPermissionTitle;
        final int requestPermissionMessage;

        switch (itemId) {
            case R.id.backupData:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                requestCode = REQUEST_WRITE_EXTERNAL_STORAGE;
                requestPermissionTitle = R.string.request_write_external_storage_permission_title;
                requestPermissionMessage = R.string.request_write_external_storage_permission_message;
                break;
            case R.id.restoreData:
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                requestCode = REQUEST_READ_EXTERNAL_STORAGE;
                requestPermissionTitle = R.string.request_read_external_storage_permission_title;
                requestPermissionMessage = R.string.request_read_external_storage_permission_message;
                break;
            default:
                return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            doAction(requestCode);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(requestPermissionTitle)
                        .setMessage(requestPermissionMessage)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        ActivityCompat.requestPermissions(OptionsActivity.this, new String[]{permission}, requestCode);
                                    }
                                }).setNegativeButton(android.R.string.cancel, null);
                builder.create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            doAction(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doAction(requestCode);
        } else {
            log(Arrays.toString(permissions) + ": Permission was denied or request was cancelled");
        }
    }

    private void doAction(int requestCode) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                backupAction();
                break;
            case REQUEST_READ_EXTERNAL_STORAGE:
                restoreAction();
                break;
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
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    clearAllData();
                                }
                            }).setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        }
    }

    private List<String> getFilesList() {
        List<String> list = new ArrayList<>();
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
                                    public void onClick(DialogInterface dialog, int which) {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, R.id.spinnerItemTextView, getFilesList());
        textView.setAdapter(adapter);
        textView.setText(getString(R.string.backupFileName, LocalDate.now()
                .toString()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enterFileNameToWrite)
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String filename = textView.getText().toString();
                                if (filename.endsWith(EXT)) {
                                    filename = filename.substring(0,
                                            filename.lastIndexOf(EXT));
                                }

                                backupWithConfirmation(filename);
                            }
                        }).setNegativeButton(android.R.string.cancel, null);
        final AlertDialog dialog = builder.create();

        textView.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String filename = textView.getText().toString();
                    if (filename.endsWith(EXT)) {
                        filename = filename.substring(0,
                                filename.lastIndexOf(EXT));
                    }

                    if (filename.equals("")) {
                        Toast.makeText(OptionsActivity.this,
                                R.string.enterFileNameToWrite,
                                Toast.LENGTH_LONG).show();
                        return false;
                    }

                    dialog.dismiss();

                    backupWithConfirmation(filename);
                    return true;
                }

                return false;
            }
        });

        dialog.show();
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
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
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
                                public void onClick(DialogInterface dialog, int which) {
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
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    restore(absoluteFileName);
                                }
                            }).setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        } else {
            restore(absoluteFileName);
        }
    }
}
