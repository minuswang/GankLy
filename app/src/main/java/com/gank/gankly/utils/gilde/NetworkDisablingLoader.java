package com.gank.gankly.utils.gilde;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

public class NetworkDisablingLoader<T> implements StreamModelLoader<T> {
    @Override
    public DataFetcher<InputStream> getResourceFetcher(final T model, int width, int height) {
        return new NetworkDisablingFetcher(model);
    }
}