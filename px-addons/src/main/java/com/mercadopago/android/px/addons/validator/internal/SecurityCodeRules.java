package com.mercadopago.android.px.addons.validator.internal;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.EscValidationData;
import java.util.ArrayList;
import java.util.List;

public class SecurityCodeRules extends RuleSet<EscValidationData> {

    @NonNull private final ESCManagerBehaviour escManagerBehaviour;

    /* default */ SecurityCodeRules(@NonNull final ESCManagerBehaviour escManagerBehaviour) {
        this.escManagerBehaviour = escManagerBehaviour;
    }

    @Override
    protected Operator applyWithOperator() {
        // We applied the rules with OR operator, which means that at leas one rule has to be true
        return Operator.OR;
    }

    @Override
    List<Rule<EscValidationData>> getRules() {
        final List<Rule<EscValidationData>> rules = new ArrayList<>();
        rules.add(this::isNotCard);
        rules.add(this::hasEsc);
        return rules;
    }

    /**
     * @return true if the payment method used is not a card.
     */
    private boolean isNotCard(@NonNull final EscValidationData data) {
        return !data.isCard();
    }

    /**
     * @return true if we have esc for the payment method used.
     */
    private boolean hasEsc(@NonNull final EscValidationData data) {
        return new EscRules(escManagerBehaviour).apply(data);
    }
}