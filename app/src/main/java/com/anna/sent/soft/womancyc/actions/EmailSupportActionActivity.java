package com.anna.sent.soft.womancyc.actions;

import com.anna.sent.soft.action.EmailActionActivity;
import com.anna.sent.soft.womancyc.R;

public class EmailSupportActionActivity extends EmailActionActivity {
    @Override
    protected String getEmail() {
        return getString(R.string.supportEmail);
    }

    @Override
    protected String getSubject() {
        return getString(R.string.app_name);
    }

    @Override
    protected String getText() {
        return null;
    }

    @Override
    protected int getErrorStringResourceId() {
        return R.string.sendto_app_not_available;
    }
}
