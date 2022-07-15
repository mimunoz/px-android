package com.mercadopago.android.px.addons.validator.internal;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.SecurityValidationData;
import java.util.ArrayList;
import java.util.List;

public final class SecurityRules extends RuleSet<SecurityValidationData> {

    private final ESCManagerBehaviour escManagerBehaviour;

    public SecurityRules(@NonNull final ESCManagerBehaviour escManagerBehaviour) {
        this.escManagerBehaviour = escManagerBehaviour;
    }

    @Override
    protected Operator applyWithOperator() {
        return Operator.AND;
    }

    @Override
    public List<Rule<SecurityValidationData>> getRules() {
        final List<Rule<SecurityValidationData>> rules = new ArrayList<>();
        rules.add(this::validateEscData);
        return rules;
    }

    /**
     * @return true if there isn't data to validate or if with this data we can ask for biometrics
     */
    private boolean validateEscData(@NonNull final SecurityValidationData data) {
        return data.getEscValidationData() == null ||
            new SecurityCodeRules(escManagerBehaviour).apply(data.getEscValidationData());
    }
}