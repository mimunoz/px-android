package com.mercadopago.android.px.internal.features.payment_result.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.Instruction;

public class PaymentResultLegacyViewModel {

    public final PaymentModel model;
    public final PaymentResultScreenConfiguration configuration;
    public final Instruction instruction;

    public PaymentResultLegacyViewModel(@NonNull final PaymentModel model,
        @NonNull final PaymentResultScreenConfiguration configuration, @Nullable final Instruction instruction) {
        this.model = model;
        this.configuration = configuration;
        this.instruction = instruction;
    }
}