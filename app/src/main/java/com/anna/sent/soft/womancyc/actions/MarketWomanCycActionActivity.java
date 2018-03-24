package com.anna.sent.soft.womancyc.actions;

import com.anna.sent.soft.action.MarketActionActivity;
import com.anna.sent.soft.womancyc.R;

public class MarketWomanCycActionActivity extends MarketActionActivity {
    @Override
    protected String getAppName() {
        return "com.anna.sent.soft.womancyc";
    }

    @Override
    protected int getErrorStringResourceId() {
        return R.string.market_app_not_available;
    }
}
