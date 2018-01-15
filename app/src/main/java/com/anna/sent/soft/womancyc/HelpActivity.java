package com.anna.sent.soft.womancyc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.anna.sent.soft.utils.ActionBarUtils;
import com.anna.sent.soft.utils.NavigationUtils;
import com.anna.sent.soft.womancyc.adapters.HelpPagerAdapter;
import com.anna.sent.soft.womancyc.base.WcActivity;

public final class HelpActivity extends WcActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.help);
        setContentView(R.layout.activity_help);
        ActionBarUtils.setupActionBar(this);

        ViewPager viewPager = findViewById(R.id.pager);
        HelpPagerAdapter tabsAdapter = new HelpPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);
        viewPager.setOffscreenPageLimit(tabsAdapter.getCount() - 1);
        viewPager.setCurrentItem(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavigationUtils.navigateUp(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
