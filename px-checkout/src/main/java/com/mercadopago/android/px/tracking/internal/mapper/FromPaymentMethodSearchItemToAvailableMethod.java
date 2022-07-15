package com.mercadopago.android.px.tracking.internal.mapper;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.tracking.internal.model.AvailableMethod;

public class FromPaymentMethodSearchItemToAvailableMethod extends Mapper<PaymentMethodSearchItem, AvailableMethod> {

    @NonNull private final PaymentMethodSearch all;

    public FromPaymentMethodSearchItemToAvailableMethod(@NonNull final PaymentMethodSearch all) {
        this.all = all;
    }

    @Override
    public AvailableMethod map(@NonNull final PaymentMethodSearchItem val) {
        if (val.isPaymentMethod()) {
            final PaymentMethod paymentMethodBySearchItem = all.getPaymentMethodBySearchItem(val);
            return new AvailableMethod(paymentMethodBySearchItem.getId(), paymentMethodBySearchItem.getPaymentTypeId());
        } else if (val.isPaymentType()) {
            return new AvailableMethod(null, val.getId());
        } else {
            return new AvailableMethod(null, val.getId());
        }
    }
}
