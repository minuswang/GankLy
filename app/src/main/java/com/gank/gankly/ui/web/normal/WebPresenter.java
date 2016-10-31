package com.gank.gankly.ui.web.normal;

import android.support.annotation.NonNull;

import com.gank.gankly.data.entity.ReadHistory;
import com.gank.gankly.data.entity.UrlCollect;
import com.gank.gankly.mvp.BasePresenter;
import com.gank.gankly.mvp.source.LocalDataSource;
import com.gank.gankly.utils.ListUtils;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Create by LingYan on 2016-10-27
 * Email:137387869@qq.com
 */

public class WebPresenter extends BasePresenter implements WebContract.Presenter {
    private LocalDataSource mTask;
    private WebContract.View mView;
    private long endTime;
    private List<UrlCollect> mCollects;
    private Subscription subscription;

    public WebPresenter(LocalDataSource task, WebContract.View view) {
        mTask = task;
        mView = view;
        mCollects = new ArrayList<>();
    }

    @Override
    public void findCollectUrl(@NonNull String url) {
        mTask.findUrlCollect(url).subscribe(new Subscriber<List<UrlCollect>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<UrlCollect> urlCollects) {
                mCollects = urlCollects;
                boolean isCollect = ListUtils.getListSize(urlCollects) > 0;
                mView.setCollectIcon(isCollect);
            }
        });
    }

    @Override
    public void findHistoryUrl(@NonNull final String url) {
        mTask.findReadHistory(url).subscribe(new Subscriber<List<ReadHistory>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<ReadHistory> readHistories) {
                if (ListUtils.getListSize(readHistories) <= 0) {
                    UrlCollect urlCollect = mView.getCollect();
                    if (urlCollect != null) {
                        ReadHistory readHistory = new ReadHistory();
                        readHistory.setDate(new Date());
                        readHistory.setComment(urlCollect.getComment());
                        readHistory.setUrl(url);
                        readHistory.setG_type(urlCollect.getG_type());
                        mTask.insertReadHistory(readHistory);
                    }
                }
            }
        });
    }

    @Override
    public void cancelCollect() {
        if (ListUtils.getListSize(mCollects) <= 0) {
            return;
        }
        long deleteByKey = mCollects.get(0).getId();
        mTask.cancelCollect(deleteByKey).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                KLog.e(e);
            }

            @Override
            public void onNext(String string) {
                KLog.d("cancelCollect:" + string);
            }
        });
    }

    @Override
    public void collect() {
        UrlCollect urlCollect = mView.getCollect();
        mTask.insertCollect(urlCollect).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                KLog.d("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                KLog.e(e);
            }

            @Override
            public void onNext(Long aLong) {
                KLog.d("收藏成功，aLong:" + aLong);
            }
        });
    }

    @Override
    public void collectAction(final boolean isCollect) {
        long curTime = System.currentTimeMillis();
        long subTime = curTime - endTime;
        KLog.d("subTime:" + subTime);
        if (curTime - endTime < 2000) {
            subscription.unsubscribe();
        }
        endTime = curTime;

        subscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(isCollect);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .delay(1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        KLog.d("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.e(e);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        KLog.d("collectAction，aBoolean:" + aBoolean);
                        if (aBoolean) {
                            collect();
                        } else {
                            cancelCollect();
                        }
                    }
                });

        mRxManager.add(subscription);
    }


    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {
        mRxManager.clear();
    }
}