package com.mercadopago.android.px.internal.viewmodel.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import kotlin.Pair;

public class PaymentMethodMapper extends Mapper<Pair<String, String>, PaymentMethod> {

    @NonNull private final PaymentMethodSearch paymentMethodSearch;

    public PaymentMethodMapper(@NonNull final PaymentMethodSearch paymentMethodSearch) {
        this.paymentMethodSearch = paymentMethodSearch;
    }

    @Override
    public PaymentMethod map(@NonNull final Pair<String, String> paymentMethod) {
        PaymentMethod paymentMethodById = paymentMethodSearch.getPaymentMethodById(paymentMethod.getFirst());
        if (paymentMethodById != null) {
            paymentMethodById.setPaymentTypeId(paymentMethod.getSecond());
        }
        return paymentMethodById;
    }
}