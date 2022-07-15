package com.mercadopago.android.px.model;

import androidx.annotation.NonNull;

/**
 *
 */
public interface IPaymentDescriptorHandler {

    void visit(@NonNull final IPaymentDescriptor payment);

    void visit(@NonNull final BusinessPayment businessPayment);
}
