package com.mercadopago.android.px.internal.features.business_result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;
import com.mercadopago.android.px.model.ExitAction;

/* default */ class BusinessPaymentResultViewModel {

    /* default */ final PaymentResultHeader.Model headerModel;
    /* default */ final PaymentResultBody.Model bodyModel;
    /* default */ final ExitAction primaryAction;
    /* default */ final ExitAction secondaryAction;

    /* default */ BusinessPaymentResultViewModel(@NonNull final PaymentResultHeader.Model headerModel,
        @NonNull final PaymentResultBody.Model bodyModel,
        @Nullable final ExitAction primaryAction,
        @Nullable final ExitAction secondaryAction) {
        this.headerModel = headerModel;
        this.bodyModel = bodyModel;
        this.primaryAction = primaryAction;
        this.secondaryAction = secondaryAction;
    }
}