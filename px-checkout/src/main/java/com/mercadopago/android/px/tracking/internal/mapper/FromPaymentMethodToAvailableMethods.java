package com.mercadopago.android.px.tracking.internal.mapper;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.tracking.internal.model.AvailableMethod;

public class FromPaymentMethodToAvailableMethods extends Mapper<PaymentMethod, AvailableMethod> {
    @Override
    public AvailableMethod map(@NonNull final PaymentMethod paymentMethod) {
        return new AvailableMethod(paymentMethod.getId(), paymentMethod.getPaymentTypeId());
    }
}
