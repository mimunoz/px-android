package com.mercadopago.android.px.internal.viewmodel.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.features.payment_vault.SearchItemOnClickListenerHandler;

public abstract class SearchOptionViewModelMapper<T, V> extends Mapper<T, V> {
    @NonNull protected SearchItemOnClickListenerHandler handler;

    /* default */ SearchOptionViewModelMapper(@NonNull final SearchItemOnClickListenerHandler handler) {
        this.handler = handler;
    }
}