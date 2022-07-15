package com.mercadopago.android.px.addons.validator.internal;

import androidx.annotation.NonNull;

public interface Rule<T> {
    boolean apply(@NonNull final T data);
}