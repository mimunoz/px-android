package com.mercadopago.android.px.internal.repository;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;
import java.util.Collection;
import java.util.Map;

public interface DisabledPaymentMethodRepository {

    boolean hasPaymentMethodId(@NonNull final String paymentMethodId);

    void handleDisableablePayment(@NonNull final PaymentResult paymentResult);

    void storeDisabledPaymentMethodsIds(@NonNull Collection<String> paymentMethodsIds);

    DisabledPaymentMethod getDisabledPaymentMethod(@NonNull final String paymentMethodId);

    /**
     * @return a map of disabled payment methods with payment method id as key and DisabledPaymentMethod as value.
     */
    Map<String, DisabledPaymentMethod> getDisabledPaymentMethods();

    void reset();
}