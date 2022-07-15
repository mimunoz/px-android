package com.mercadopago.android.px.internal.features.payment_vault;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;

public interface SearchItemOnClickListenerHandler {
    void selectItem(@NonNull final CustomSearchItem item);

    void selectItem(@NonNull final PaymentMethodSearchItem item);

    void showDisabledPaymentMethodDetailDialog(@NonNull final DisabledPaymentMethod disabledPaymentMethod);
}