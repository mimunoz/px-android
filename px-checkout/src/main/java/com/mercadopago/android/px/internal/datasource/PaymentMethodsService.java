package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.repository.PaymentMethodsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.services.CheckoutService;
import com.mercadopago.android.px.model.PaymentMethod;
import java.util.List;

import static com.mercadopago.android.px.services.BuildConfig.API_ENVIRONMENT;

public class PaymentMethodsService implements PaymentMethodsRepository {

    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final CheckoutService checkoutService;

    public PaymentMethodsService(
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final CheckoutService checkoutService) {
        this.paymentSettingRepository = paymentSettingRepository;
        this.checkoutService = checkoutService;
    }

    @Override
    public MPCall<List<PaymentMethod>> getPaymentMethods() {
        return checkoutService.getPaymentMethods(API_ENVIRONMENT, paymentSettingRepository.getPublicKey(),
            paymentSettingRepository.getPrivateKey());
    }
}