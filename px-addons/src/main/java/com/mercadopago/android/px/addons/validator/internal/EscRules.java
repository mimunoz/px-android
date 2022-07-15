package com.mercadopago.android.px.addons.validator.internal;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.EscValidationData;
import java.util.ArrayList;
import java.util.List;

/* default */ final class EscRules extends RuleSet<EscValidationData> {

    private final ESCManagerBehaviour escManagerBehaviour;

    /* default */ EscRules(@NonNull final ESCManagerBehaviour escManagerBehaviour) {
        this.escManagerBehaviour = escManagerBehaviour;
    }

    @Override
    protected Operator applyWithOperator() {
        // We applied the rules with AND operator, which means that every rule has to be true
        return Operator.AND;
    }

    @Override
    public List<Rule<EscValidationData>> getRules() {
        final List<Rule<EscValidationData>> rules = new ArrayList<>();
        rules.add(this::isEscEnable);
        rules.add(this::hasCardId);
        rules.add(this::hasEsc);
        return rules;
    }

    /**
     * @return true if the current payment flow has esc feature enable.
     */
    private boolean isEscEnable(final EscValidationData data) {
        return data.isEscEnable();
    }

    /**
     * @return true card id is not null or empty.
     */
    private boolean hasCardId(final EscValidationData data) {
        return data.getCardId() != null && !data.getCardId().isEmpty();
    }

    /**
     * @return true there is esc for the given card id in the data.
     */
    private boolean hasEsc(final EscValidationData data) {
        final String esc = escManagerBehaviour.getESC(data.getCardId(), null, null);
        return esc != null && !esc.isEmpty();
    }
}