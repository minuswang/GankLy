package com.gank.gankly.ui.main.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.gank.gankly.App;
import com.gank.gankly.R;
import com.gank.gankly.RxBus.ChangeThemeEvent.ThemeEvent;
import com.gank.gankly.RxBus.RxBus;
import com.gank.gankly.bean.ResultsBean;
import com.gank.gankly.config.Constants;
import com.gank.gankly.listener.RecyclerOnClick;
import com.gank.gankly.mvp.source.remote.RemoteDataSource;
import com.gank.gankly.ui.base.LazyFragment;
import com.gank.gankly.ui.base.LySwipeRefreshLayout;
import com.gank.gankly.ui.main.GankAdapter;
import com.gank.gankly.ui.main.HomeActivity;
import com.gank.gankly.ui.web.WebActivity;
import com.gank.gankly.utils.CircularAnimUtils;
import com.gank.gankly.utils.StyleUtils;
import com.gank.gankly.widget.LYRelativeLayoutRipple;
import com.gank.gankly.widget.MultipleStatusView;

import java.util.List;

import butterknife.BindView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import rx.functions.Action1;

/**
 * Android
 * Create by LingYan on 2016-4-26
 * Email:137387869@qq.com
 */
public class AndroidFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerOnClick, AndroidContract.View {
    @BindView(R.id.multiple_status_view)
    MultipleStatusView mMultipleStatusView;
    @BindView(R.id.swipe_refresh)
    LySwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;
    private HomeActivity mActivity;
    private GankAdapter mGankAdapter;
    private AndroidContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_swiperefresh_multiple_status;
    }

    public AndroidFragment() {
    }

    public static AndroidFragment newInstance() {
        AndroidFragment fragment = new AndroidFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new AndroidPresenter(RemoteDataSource.getInstance(), this);
    }

    @Override
    protected void initValues() {
        setMultipleStatusView(mMultipleStatusView);
        setSwipeRefreshLayout(mSwipeRefreshLayout);

        RxBus.getInstance().toSubscription(ThemeEvent.class, new Action1<ThemeEvent>() {
            @Override
            public void call(ThemeEvent event) {
                refreshUi();
            }
        });
    }

    @Override
    protected void initViews() {
        mGankAdapter = new GankAdapter(mActivity);
        mGankAdapter.setOnItemClickListener(this);
        initRecycler();
    }

    @Override
    protected void bindLister() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mMultipleStatusView.setListener(new MultipleStatusView.OnMultipleClick() {
            @Override
            public void retry(View v) {
                showLoading();
                mPresenter.fetchNew();
            }
        });
    }

    @Override
    protected void initData() {
        showLoading();
        onDownRefresh();
    }

    private void initRecycler() {
        mRecyclerView = mSwipeRefreshLayout.getRecyclerView();
        mSwipeRefreshLayout.setLayoutManager(new LinearLayoutManager(mActivity));
//        mRecyclerView.addItemDecoration(new RecycleViewDivider(mActivity, R.drawable.shape_item_divider));
        mSwipeRefreshLayout.setOnScrollListener(new LySwipeRefreshLayout.OnSwipeRefRecyclerViewListener() {
            @Override
            public void onRefresh() {
                showRefresh();
                mPresenter.fetchNew();
            }

            @Override
            public void onLoadMore() {
                showRefresh();
                mPresenter.fetchMore();
            }
        });

        mSwipeRefreshLayout.getRecyclerView().setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mGankAdapter);
        alphaAdapter.setFirstOnly(true);
        alphaAdapter.setDuration(500);
        alphaAdapter.setInterpolator(new OvershootInterpolator(0.5f));
        mSwipeRefreshLayout.setAdapter(alphaAdapter);
    }

    private void onDownRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        fetchDate();
    }

    private void fetchDate() {
        mPresenter.fetchNew();
    }

    @Override
    public void onRefresh() {
        onDownRefresh();
    }

    @Override
    public void onClick(View view, ResultsBean bean) {
        Bundle bundle = new Bundle();
        bundle.putString(WebActivity.TITLE, bean.getDesc());
        bundle.putString(WebActivity.URL, bean.getUrl());
        bundle.putString(WebActivity.TYPE, Constants.ANDROID);
        bundle.putString(WebActivity.AUTHOR, bean.getWho());
        Intent intent = new Intent(mActivity, WebActivity.class);
        intent.putExtras(bundle);
        CircularAnimUtils.startActivity(mActivity, intent, view, R.color.white_half);
    }

    @Override
    public void refillDate(List<ResultsBean> list) {
        mGankAdapter.updateItems(list);
    }

    @Override
    public void appendData(List<ResultsBean> list) {
        mGankAdapter.appendMoreDate(list);
    }

    @Override
    public void hideRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void hasNoMoreDate() {
        Snackbar.make(mSwipeRefreshLayout, R.string.loading_no_more, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showRefreshError(String errorStr) {
        Snackbar.make(mSwipeRefreshLayout, errorStr, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void refreshUi() {
        Resources.Theme theme = mActivity.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.baseAdapterItemTextColor, typedValue, true);
        int textColor = typedValue.data;
        theme.resolveAttribute(R.attr.textSecondaryColor, typedValue, true);
        int textSecondaryColor = typedValue.data;
        theme.resolveAttribute(R.attr.themeBackground, typedValue, true);
        int mainColor = typedValue.data;
        mRecyclerView.setBackgroundColor(mainColor);
        theme.resolveAttribute(R.attr.androidItemTimeIcon, typedValue, true);
        int leftResource = typedValue.resourceId;

        int childCount = mRecyclerView.getChildCount();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            ViewGroup childView = (ViewGroup) mRecyclerView.getChildAt(childIndex);
            LYRelativeLayoutRipple rl = (LYRelativeLayoutRipple) childView.findViewById(R.id.welfare_rl);
            rl.setCustomBackgroundResource(R.attr.lyItemSelectBackground);

            TextView title = (TextView) childView.findViewById(R.id.goods_txt_title);
            title.setTextColor(textColor);
            TextView time = (TextView) childView.findViewById(R.id.goods_txt_time);
            time.setTextColor(textSecondaryColor);

            Drawable drawable = App.getAppResources().getDrawable(leftResource);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                time.setCompoundDrawables(drawable, null, null, null);
            }
        }

        StyleUtils.clearRecyclerViewItem(mRecyclerView);
        StyleUtils.changeSwipeRefreshLayout(mSwipeRefreshLayout);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (HomeActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setMultipleStatusView(mMultipleStatusView);
    }

    private ColorStateList getSwitchThumbColorStateList() {
        int mSelectColor;
        int unSelectColor;

        if (App.isNight()) {
            mSelectColor = R.color.switch_thumb_disabled_dark;
            unSelectColor = R.color.navigation_item_icon;
        } else {
            mSelectColor = R.color.colorAccent;
            unSelectColor = R.color.gray;
        }

        final int[][] states = new int[3][];
        final int[] colors = new int[3];

        // Disabled state
        states[0] = new int[]{-android.R.attr.state_enabled};
        colors[0] = (Color.DKGRAY);

        // Checked state
        states[1] = new int[]{android.R.attr.state_checked};

        colors[1] = App.getAppColor(mSelectColor);

        // Unchecked enabled state state
        states[2] = new int[0];

        colors[2] = App.getAppColor(unSelectColor);

        return new ColorStateList(states, colors);
    }
}