package com.mercadopago.android.px.utils;

import androidx.annotation.NonNull;
import com.mercadopago.SamplePaymentProcessor;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.core.SplitPaymentProcessor;

public final class PaymentConfigurationUtils {

    private PaymentConfigurationUtils() {
        //Do nothing
    }

    public static PaymentConfiguration create(@NonNull final SplitPaymentProcessor paymentProcessor) {
        return new PaymentConfiguration.Builder(paymentProcessor).build();
    }

    public static PaymentConfiguration create() {
        return create(new SamplePaymentProcessor(PaymentUtils.getBusinessPaymentApproved()));
    }
}