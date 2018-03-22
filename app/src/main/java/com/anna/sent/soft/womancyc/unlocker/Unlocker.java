package com.anna.sent.soft.womancyc.unlocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.shared.Settings;

public class Unlocker extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.anna.sent.soft.womancyc.unlocker.Unlocker.clearPassword".equals(intent.getAction())) {
            Settings.clearPassword(context);
            Toast.makeText(context, getClass().getSimpleName() + ": " + context.getString(R.string.passwordIsNotSet),
                    Toast.LENGTH_LONG).show();
        }
    }
}
