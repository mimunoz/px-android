package com.mercadopago.android.px.internal.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.PaymentMethod;

public final class PaymentMethodHelper {

    private PaymentMethodHelper() {
    }

    @Nullable
    /* default */ static PaymentMethod getPaymentMethodById(@NonNull final Iterable<PaymentMethod> paymentMethods,
        @NonNull final String paymentMethodId) {
        for (final PaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethodId.equals(paymentMethod.getId())) {
                return paymentMethod;
            }
        }
        return null;
    }

    @Nullable
    public static PaymentMethod assembleOfflinePaymentMethod(@NonNull final Iterable<PaymentMethod> paymentMethods,
        final String paymentMethodId, final String paymentTypeId) {
        final PaymentMethod offlinePaymentMethod = getPaymentMethodById(paymentMethods, paymentMethodId);
        if (offlinePaymentMethod != null) {
            offlinePaymentMethod.setPaymentTypeId(paymentTypeId);
        }
        return offlinePaymentMethod;
    }
}