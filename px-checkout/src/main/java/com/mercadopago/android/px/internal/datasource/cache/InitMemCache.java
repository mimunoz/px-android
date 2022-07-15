package com.mercadopago.android.px.internal.datasource.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.services.Callback;

public class InitMemCache implements Cache<InitResponse> {

    @Nullable private InitResponse initResponse;

    @NonNull
    @Override
    public MPCall<InitResponse> get() {
        return callback -> resolve(callback);
    }

    /* default */ void resolve(final Callback<InitResponse> callback) {
        if (isCached()) {
            callback.success(initResponse);
        } else {
            callback.failure(new ApiException());
        }
    }

    @Override
    public void put(@NonNull final InitResponse initResponse) {
        this.initResponse = initResponse;
    }

    @Override
    public void evict() {
        initResponse = null;
    }

    @Override
    public boolean isCached() {
        return initResponse != null;
    }
}