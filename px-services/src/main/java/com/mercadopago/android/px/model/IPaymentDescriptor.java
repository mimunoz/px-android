package com.mercadopago.android.px.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public interface IPaymentDescriptor extends IPayment {

    /**
     * @return payment type id - associated with this payment
     */
    @Nullable
    String getPaymentTypeId();

    /**
     * @return payment method id - associated with this payment
     */
    @Nullable
    String getPaymentMethodId();

    /**
     * @return payment ids - associated with this payment including split payments
     */
    @Nullable
    default List<String> getPaymentIds() {
        return getId() != null ? Collections.singletonList(String.valueOf(getId())) : null;
    }

    /**
     * method to visit {@link IPaymentDescriptor} implementation.
     *
     * @param handler
     */
    default void process(@NonNull final IPaymentDescriptorHandler handler) {
        handler.visit(this);
    }
}