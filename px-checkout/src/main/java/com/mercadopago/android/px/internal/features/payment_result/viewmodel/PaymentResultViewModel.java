package com.mercadopago.android.px.internal.features.payment_result.viewmodel;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesModel;
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.PaymentResultFooter;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;

public class PaymentResultViewModel {

    public final PaymentResultHeader.Model headerModel;
    public final RemediesModel remediesModel;
    public final PaymentResultBody.Model bodyModel;
    public final PaymentResultFooter.Model footerModel;
    public final PaymentResultLegacyViewModel legacyViewModel;

    public PaymentResultViewModel(@NonNull final PaymentResultHeader.Model headerModel,
        @NonNull final RemediesModel remediesModel,
        @NonNull final PaymentResultFooter.Model footerModel,
        @NonNull final PaymentResultBody.Model bodyModel,
        @NonNull final PaymentResultLegacyViewModel legacyViewModel) {
        this.headerModel = headerModel;
        this.remediesModel = remediesModel;
        this.footerModel = footerModel;
        this.bodyModel = bodyModel;
        this.legacyViewModel = legacyViewModel;
    }
}