package com.mercadopago.android.px.internal.datasource.cache;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.services.Callback;

public class InitCacheCoordinator implements Cache<InitResponse> {

    @NonNull /* default */ final InitDiskCache initDiskCache;
    @NonNull /* default */ final InitMemCache initMemCache;

    public InitCacheCoordinator(@NonNull final InitDiskCache initDiskCache,
        @NonNull final InitMemCache initMemCache) {
        this.initDiskCache = initDiskCache;
        this.initMemCache = initMemCache;
    }

    @NonNull
    @Override
    public MPCall<InitResponse> get() {
        if (initMemCache.isCached()) {
            return initMemCache.get();
        } else {
            return callback -> initDiskCache.get().enqueue(getCallbackDisk(callback));
        }
    }

    /* default */ Callback<InitResponse> getCallbackDisk(final Callback<InitResponse> callback) {
        return new Callback<InitResponse>() {
            @Override
            public void success(final InitResponse initResponse) {
                initMemCache.put(initResponse);
                callback.success(initResponse);
            }

            @Override
            public void failure(final ApiException apiException) {
                callback.failure(apiException);
            }
        };
    }

    @Override
    public void put(@NonNull final InitResponse initResponse) {
        initMemCache.put(initResponse);
        initDiskCache.put(initResponse);
    }

    @Override
    public void evict() {
        initDiskCache.evict();
        initMemCache.evict();
    }

    @Override
    public boolean isCached() {
        return initMemCache.isCached() || initDiskCache.isCached();
    }
}