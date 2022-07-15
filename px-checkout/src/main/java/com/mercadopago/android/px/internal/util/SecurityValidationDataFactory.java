package com.mercadopago.android.px.internal.util;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.model.EscValidationData;
import com.mercadopago.android.px.addons.model.SecurityValidationData;
import com.mercadopago.android.px.internal.core.ProductIdProvider;
import com.mercadopago.android.px.model.internal.PaymentConfiguration;
import java.math.BigDecimal;

public final class SecurityValidationDataFactory {

    private SecurityValidationDataFactory() {
    }

    public static SecurityValidationData create(@NonNull final ProductIdProvider productIdProvider,
        @NonNull final BigDecimal totalAmount, @NonNull final PaymentConfiguration paymentConfiguration) {
        final String productId = productIdProvider.getProductId();
        final String customOptionId = paymentConfiguration.getCustomOptionId();
        final boolean isCard = paymentConfiguration.isCard();
        final EscValidationData escValidationData = new EscValidationData.Builder(customOptionId, isCard).build();
        return new SecurityValidationData.Builder(productId).putParam("amount", totalAmount)
            .setEscValidationData(escValidationData).build();
    }
}