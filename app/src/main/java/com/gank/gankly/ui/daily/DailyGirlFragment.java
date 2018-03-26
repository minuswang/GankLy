package com.gank.gankly.ui.daily;

import android.app.ProgressDialog;
import android.content.Intent;
import android.ly.business.domain.Girl;
import android.ly.business.domain.Gift;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.gank.gankly.R;
import com.gank.gankly.listener.ItemClick;
import com.gank.gankly.ui.base.fragment.LazyFragment;
import com.gank.gankly.ui.gallery.GalleryActivity;
import com.gank.gankly.widget.LySwipeRefreshLayout;
import com.gank.gankly.widget.MultipleStatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 妹子 - 每日更新
 * Create by LingYan on 2016-07-01
 */
public class DailyGirlFragment extends LazyFragment implements DailyGirlContract.View {
    @BindView(R.id.multiple_status_view)
    MultipleStatusView multipleStatusView;

    @BindView(R.id.swipe_refresh)
    LySwipeRefreshLayout swipeRefresh;

    private DailyGirlAdapter dailyGirlAdapter;
    private DailyGirlPresenter dailyPresenter;

    private ProgressDialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_gift;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dailyGirlAdapter = new DailyGirlAdapter();
        swipeRefresh.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        swipeRefresh.setAdapter(dailyGirlAdapter);

        swipeRefresh.setOnScrollListener(new LySwipeRefreshLayout.OnSwipeRefreshListener() {
            @Override
            public void onRefresh() {
                dailyPresenter.fetchNew();
            }

            @Override
            public void onLoadMore() {
            }
        });

        dailyGirlAdapter.setOnItemClickListener(new ItemClick() {
            @Override
            public void onClick(int position, Object object) {

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dailyPresenter = new DailyGirlPresenter(context, this);
    }

    @Override
    protected void initLazy() {
        multipleStatusView.showLoading();
        dailyPresenter.fetchNew();
    }

    private void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(context);
        }

        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage(context.getString(R.string.loading_meizi_images));
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnCancelListener(dialog -> dailyPresenter.unSubscribe());

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    public void openBrowseActivity(@NonNull ArrayList<Gift> list) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(context, GalleryActivity.class);
        bundle.putString(GalleryActivity.EXTRA_MODEL, GalleryActivity.EXTRA_DAILY);
        intent.putExtra(GalleryActivity.EXTRA_LIST, list);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void hideProgress() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void hasNoMoreDate() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void showContent() {
        multipleStatusView.showContent();
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showDisNetWork() {

    }

    @Override
    public void showError() {

    }

    private void showLoading() {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
        }
    }

//    @Override
    public void onClick(int position, Object object) {
//        DailyGirl dailyGirl = (DailyGirl) object;
//        String url = dailyGirl.getUrl();
//        if (!TextUtils.isEmpty(url)) {
//            showLoadingDialog();
//            dailyPresenter.girlsImages(url);
//        }
    }

    @Override
    public void refillData(List<Girl> list) {
        dailyGirlAdapter.refillItem(list);
    }

    @Override
    public void appendItem(List<Girl> list) {
        dailyGirlAdapter.appendItem(list);
    }

    @Override
    public void setMaxProgress(int value) {
        if (mDialog != null) {
            mDialog.setMax(value);
        }
    }

    @Override
    public void disProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dailyGirlAdapter != null) {
            dailyGirlAdapter.destroy();
        }
    }
}