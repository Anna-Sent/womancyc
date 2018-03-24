package com.anna.sent.soft.womancyc.actions;

import com.anna.sent.soft.action.EmailActionActivity;
import com.anna.sent.soft.utils.UserEmailFetcher;
import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.shared.Settings;

public class EmailPasswordActionActivity extends EmailActionActivity {
    @Override
    protected String getEmail() {
        return UserEmailFetcher.getEmail(this);
    }

    @Override
    protected String getSubject() {
        return getString(R.string.app_name);
    }

    @Override
    protected String getText() {
        return Settings.getPassword(this);
    }

    @Override
    protected int getErrorStringResourceId() {
        return R.string.sendto_app_not_available;
    }
}
