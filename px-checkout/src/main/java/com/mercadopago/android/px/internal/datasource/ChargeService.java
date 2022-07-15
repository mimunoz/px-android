package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.ChargeRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule;
import java.math.BigDecimal;

public class ChargeService implements ChargeRepository {

    @NonNull private final PaymentSettingRepository configuration;

    public ChargeService(@NonNull final PaymentSettingRepository configuration) {
        this.configuration = configuration;
    }

    @Override
    @NonNull
    public BigDecimal getChargeAmount(@NonNull final String paymentTypeId) {
        return charges(paymentTypeId);
    }

    @Nullable
    @Override
    public PaymentTypeChargeRule getChargeRule(@NonNull final String paymentTypeId) {
        for (final PaymentTypeChargeRule rule : configuration.chargeRules()) {
            if (shouldApply(paymentTypeId, rule)) {
                return rule;
            }
        }
        return null;
    }

    private boolean shouldApply(@NonNull final String paymentTypeId, @NonNull final PaymentTypeChargeRule rule) {
        return rule.getPaymentTypeId().equalsIgnoreCase(paymentTypeId);
    }

    @NonNull
    private BigDecimal charges(@NonNull final String paymentTypeId) {
        BigDecimal chargeAmount = BigDecimal.ZERO;
        for (final PaymentTypeChargeRule rule : configuration.chargeRules()) {
            if (shouldApply(paymentTypeId, rule)) {
                chargeAmount = chargeAmount.add(rule.charge());
            }
        }
        return chargeAmount;
    }
}