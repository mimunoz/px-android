package com.mercadopago.android.px.internal.viewmodel;

import android.os.Bundle;
import androidx.annotation.NonNull;

public final class CheckoutStateModel {

    private static final String EXTRA_PM_EDITED = "EXTRA_PM_EDITED";
    private static final String EXTRA_UNIQUE_PM = "EXTRA_UNIQUE_PM";
    private static final String EXTRA_IS_EXPRESS_CHECKOUT = "EXTRA_IS_EXPRESS_CHECKOUT";

    public boolean paymentMethodEdited;
    public boolean isUniquePaymentMethod;
    public boolean isExpressCheckout;

    public CheckoutStateModel() {
    }

    public void toBundle(@NonNull final Bundle bundle) {
        bundle.putBoolean(EXTRA_PM_EDITED, paymentMethodEdited);
        bundle.putBoolean(EXTRA_UNIQUE_PM, isUniquePaymentMethod);
        bundle.putBoolean(EXTRA_IS_EXPRESS_CHECKOUT, isExpressCheckout);
    }

    public static CheckoutStateModel fromBundle(@NonNull final Bundle bundle) {
        final CheckoutStateModel stateModel = new CheckoutStateModel();
        stateModel.paymentMethodEdited = bundle.getBoolean(EXTRA_PM_EDITED);
        stateModel.isUniquePaymentMethod = bundle.getBoolean(EXTRA_UNIQUE_PM);
        stateModel.isExpressCheckout = bundle.getBoolean(EXTRA_IS_EXPRESS_CHECKOUT);
        return stateModel;
    }
}
