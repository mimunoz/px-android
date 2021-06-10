package com.mercadopago.android.px.internal.features.payment_result.viewmodel;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;

public class PaymentResultLegacyViewModel {

    public final PaymentModel model;
    public final PaymentResultScreenConfiguration configuration;

    public PaymentResultLegacyViewModel(@NonNull final PaymentModel model,
        @NonNull final PaymentResultScreenConfiguration configuration) {
        this.model = model;
        this.configuration = configuration;
    }
}
