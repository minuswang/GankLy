package com.gank.gankly.ui.more;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gank.gankly.R;
import com.gank.gankly.ui.base.BaseActivity;
import com.gank.gankly.ui.collect.CollectFragment;
import com.gank.gankly.ui.history.BrowseHistoryFragment;

/**
 * Create by LingYan on 2016-09-21
 * Email:137387869@qq.com
 */

public class MoreActivity extends BaseActivity {
    private static final int CONTENT_ID = R.id.setting_frame_layout;
    public static final String TITLE = "title";
    public static final String TYPE = "from_type";

    public static final int TYPE_SETTING = 1;
    public static final int TYPE_COLLECT = 2;
    public static final int TYPE_BROWSE = 3;

    private int mType;

    @Override
    protected int getContentId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initValues() {
        parseIntent();
        Fragment fragment = getFragment(mType);
        addFragment(fragment);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void bindListener() {

    }

    private void parseIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mType = bundle.getInt(TYPE);
        }
    }

    private Fragment getFragment(int type) {
        Fragment fragment;
        switch (type) {
            case TYPE_COLLECT:
                fragment = new CollectFragment();
                break;
            case TYPE_BROWSE:
                fragment = new BrowseHistoryFragment();
                break;
            case TYPE_SETTING:
            default:
                fragment = new SettingFragment();
                break;
        }
        return fragment;
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(CONTENT_ID, fragment)
                .commitAllowingStateLoss();
    }
}
