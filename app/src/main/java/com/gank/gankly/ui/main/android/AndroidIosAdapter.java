package com.gank.gankly.ui.main.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gank.gankly.R;
import com.gank.gankly.bean.ResultsBean;
import com.gank.gankly.config.MeiziArrayList;
import com.gank.gankly.listener.RecyclerOnClick;
import com.gank.gankly.utils.DateUtils;
import com.gank.gankly.widget.RatioImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by LingYan on 2016-04-25
 * Email:137387869@qq.com
 */
public class AndroidIosAdapter extends RecyclerView.Adapter<AndroidIosAdapter.GankViewHolder> {
    public static final int LAYOUT_Android = 1;
    public static final int LAYOUT_IOS = 2;

    private final List<ResultsBean> mResults;
    private RecyclerOnClick mMeiZiOnClick;
    private Context mContext;
    public final int mLayout;

    private int mImageSize;
    private List<ResultsBean> mImagesList;

    public AndroidIosAdapter(Context context) {
        this(context, LAYOUT_Android);
    }

    public AndroidIosAdapter(Context context, int type) {
        mResults = new ArrayList<>();
        mContext = context;
        mLayout = type;
    }

    @Override
    public GankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutView(parent);
        return new GankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GankViewHolder holder, int position) {
        ResultsBean bean = mResults.get(position);
        holder.mBean = bean;
        holder.txtDesc.setText(bean.getDesc());

        Date date = DateUtils.formatDateFromStr(bean.getPublishedAt());
        holder.txtTime.setText(DateUtils.getFormatDate(date, DateUtils.TYPE_DD));
        holder.txtName.setText(bean.getWho());

        if (mImageSize != 0) {
            if (position > mImageSize) {
                position = position % mImageSize;
            } else if (position == mImageSize) {
                position = 0;
            }
        }

        if (position < mImageSize) {
            String url = mImagesList.get(position).getUrl();
            Glide.with(mContext)
                    .load(url)
                    .error(R.drawable.item_default_img)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgHead);
        }
    }

    @Override
    public void onViewRecycled(GankViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.imgHead);
    }

    private View getLayoutView(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        int resLayout;
        switch (mLayout) {
            case LAYOUT_IOS:
                resLayout = R.layout.adapter_ios;
                break;
            default:
                resLayout = R.layout.adapter_android;
                break;
        }
        return layoutInflater.inflate(resLayout, parent, false);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public void refillItems(List<ResultsBean> results) {
        int size = mResults.size();
        mResults.clear();
        notifyItemRangeRemoved(0, size);
        appendItems(results);
    }

    public void appendItems(List<ResultsBean> results) {
        shuffleImages();

        mResults.addAll(results);
        int size = mResults.size();
        notifyItemRangeInserted(size, results.size());
    }

    private void shuffleImages() {
        List<ResultsBean> list = MeiziArrayList.getInstance().getOneItemsList();
        mImagesList = new ArrayList<>(list);
        mImageSize = mImagesList.size();
        Collections.shuffle(mImagesList);
    }

    public void setOnItemClickListener(RecyclerOnClick onItemClickListener) {
        mMeiZiOnClick = onItemClickListener;
    }

    public class GankViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.goods_txt_title)
        TextView txtDesc;
        @BindView(R.id.goods_txt_author_name)
        TextView txtName;
        @BindView(R.id.goods_txt_time)
        TextView txtTime;
        @BindView(R.id.adapter_ios_ratio_img_head)
        RatioImageView imgHead;

        ResultsBean mBean;

        public GankViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mMeiZiOnClick != null) {
                mMeiZiOnClick.onClick(v, mBean);
            }
        }
    }
}
