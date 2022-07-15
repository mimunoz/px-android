package com.mercadopago.android.px.internal.util;

import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule;
import java.math.BigDecimal;

public final class ChargeRuleHelper {

    private ChargeRuleHelper() {
    }

    public static boolean isHighlightCharge(@Nullable final PaymentTypeChargeRule chargeRule) {
        return chargeRule != null && BigDecimal.ZERO.equals(chargeRule.charge()) &&
            TextUtil.isNotEmpty(chargeRule.getMessage());
    }
}