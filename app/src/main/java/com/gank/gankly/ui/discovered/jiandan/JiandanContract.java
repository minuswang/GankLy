package com.gank.gankly.ui.discovered.jiandan;

import com.gank.gankly.bean.JianDanBean;
import com.gank.gankly.mvp.IFetchPresenter;
import com.gank.gankly.mvp.IFetchView;

import java.util.List;

/**
 * Create by LingYan on 2016-11-21
 * Email:137387869@qq.com
 */

public interface JiandanContract {
    interface View extends IFetchView {
        void refillData(List<JianDanBean> list);

        void appendMoreDate(List<JianDanBean> list);
    }

    interface Presenter extends IFetchPresenter {

    }
}
